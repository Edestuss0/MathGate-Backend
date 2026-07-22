package scraper

import (
	"fmt"
	"net/url"
	"regexp"
	"strings"

	"github.com/PuerkitoBio/goquery"
	"golang.org/x/net/html"

	models "mathgate/internal/domain/exam"
)
type parsedQuestion struct {
	answer         string
	taskBlocks     []models.ExamBlock
	solutionBlocks []models.ExamBlock
}

var (
	answerPrefixRe     = regexp.MustCompile(`(?i)Ответ:\s*:?`)
	spaceBeforePunctRe = regexp.MustCompile(`\s+([.,!?])`)
	horizontalWSRe     = regexp.MustCompile(`[\t\v\f\r ]+`)
	spacesAroundNLRe   = regexp.MustCompile(` *\n *`)
	multiNLRe          = regexp.MustCompile(`\n+`)

	softSpaceReplacer = strings.NewReplacer(
		"\u00AD", "",
		"\u202f", " ",
		"\u00A0", " ",
	)
)

func parseQuestion(doc *goquery.Document, pageURL string) (*parsedQuestion, error) {
	body := doc.Find("div.pbody, div.problem_body").First()
	if body.Length() == 0 {
		return nil, fmt.Errorf("не удалось найти вопрос")
	}

	solutionRoot := doc.Find("div.solution").First()

	rawAnswer := strings.TrimSpace(doc.Find("div.answer span").First().Text())
	answer := strings.TrimSpace(answerPrefixRe.ReplaceAllString(rawAnswer, ""))

	base, err := url.Parse(pageURL)
	if err != nil {
		return nil, err
	}

	taskBlocks := postProcessBlocks(parseBlocks(body.Nodes[0], base))

	var solutionBlocks []models.ExamBlock
	if solutionRoot.Length() > 0 {
		solutionBlocks = parseBlocks(solutionRoot.Nodes[0], base)
	}
	solutionBlocks = cleanSolutionPrefix(solutionBlocks)
	solutionBlocks = postProcessBlocks(solutionBlocks)

	return &parsedQuestion{
		answer:         answer,
		taskBlocks:     taskBlocks,
		solutionBlocks: solutionBlocks,
	}, nil
}

// --- обход DOM ---

func parseBlocks(root *html.Node, base *url.URL) []models.ExamBlock {
	var out []models.ExamBlock
	for c := root.FirstChild; c != nil; c = c.NextSibling {
		parseNode(c, &out, base)
	}
	return out
}

func parseNode(node *html.Node, out *[]models.ExamBlock, base *url.URL) {
	switch node.Type {
	case html.TextNode:
		if text := cleanText(node.Data); text != "" {
			appendText(out, text)
		}

	case html.ElementNode:
		switch node.Data {
		case "br":
			if len(*out) > 0 {
				appendText(out, "\n")
			}

		case "p", "div", "center", "li", "tr":
			ensureTrailingNewline(out)
			for c := node.FirstChild; c != nil; c = c.NextSibling {
				parseNode(c, out, base)
			}
			ensureTrailingNewline(out)

		case "img":
			src := resolveImgSrc(node, base)
			if src == "" {
				return
			}
			if hasClass(node, "tex") {
				*out = append(*out, models.ExamBlock{BlockType: "FORMULA", Content: src})
			} else {
				*out = append(*out, models.ExamBlock{BlockType: "IMAGE", Content: src})
			}

		default:
			for c := node.FirstChild; c != nil; c = c.NextSibling {
				parseNode(c, out, base)
			}
		}
	}
}

func ensureTrailingNewline(out *[]models.ExamBlock) {
	if n := len(*out); n > 0 && !strings.HasSuffix((*out)[n-1].Content, "\n") {
		appendText(out, "\n")
	}
}

func appendText(out *[]models.ExamBlock, text string) {
	if n := len(*out); n > 0 && (*out)[n-1].BlockType == "TEXT" {
		(*out)[n-1].Content += text
	} else {
		*out = append(*out, models.ExamBlock{BlockType: "TEXT", Content: text})
	}
}

func cleanText(text string) string {
	return softSpaceReplacer.Replace(text)
}

func resolveImgSrc(node *html.Node, base *url.URL) string {
	src := attr(node, "src")
	if src == "" || base == nil {
		return src
	}
	ref, err := url.Parse(src)
	if err != nil {
		return src
	}
	return base.ResolveReference(ref).String()
}

func attr(node *html.Node, key string) string {
	for _, a := range node.Attr {
		if a.Key == key {
			return a.Val
		}
	}
	return ""
}

func hasClass(node *html.Node, class string) bool {
	for _, c := range strings.Fields(attr(node, "class")) {
		if c == class {
			return true
		}
	}
	return false
}

// --- пост-обработка ---

func postProcessBlocks(blocks []models.ExamBlock) []models.ExamBlock {
	processed := make([]models.ExamBlock, 0, len(blocks))

	for _, b := range blocks {
		if b.BlockType != "TEXT" {
			processed = append(processed, b)
			continue
		}
		content := b.Content
		content = spaceBeforePunctRe.ReplaceAllString(content, "$1")
		content = horizontalWSRe.ReplaceAllString(content, " ")
		content = spacesAroundNLRe.ReplaceAllString(content, "\n")
		content = multiNLRe.ReplaceAllString(content, "\n")

		if content != "" {
			processed = append(processed, models.ExamBlock{BlockType: "TEXT", Content: content})
		}
	}

	for len(processed) > 0 && processed[0].BlockType == "TEXT" && strings.TrimSpace(processed[0].Content) == "" {
		processed = processed[1:]
	}
	for len(processed) > 0 {
		last := len(processed) - 1
		if processed[last].BlockType == "TEXT" && strings.TrimSpace(processed[last].Content) == "" {
			processed = processed[:last]
		} else {
			break
		}
	}

	if len(processed) > 0 && processed[0].BlockType == "TEXT" {
		processed[0].Content = strings.TrimLeft(processed[0].Content, " \t\n\r")
	}
	if len(processed) > 0 {
		last := len(processed) - 1
		if processed[last].BlockType == "TEXT" {
			processed[last].Content = strings.TrimRight(processed[last].Content, " \t\n\r")
		}
	}

	return processed
}

func cleanSolutionPrefix(blocks []models.ExamBlock) []models.ExamBlock {
	const prefix = "решение"

	for len(blocks) > 0 {
		first := blocks[0]
		if first.BlockType != "TEXT" {
			break
		}
		content := strings.TrimLeft(first.Content, "\n ")
		if strings.HasPrefix(strings.ToLower(content), prefix) {
			runes := []rune(content)
			content = string(runes[len([]rune(prefix)):])
			content = strings.TrimLeft(content, ". \n")
		}
		if strings.TrimSpace(content) != "" {
			blocks[0] = models.ExamBlock{BlockType: first.BlockType, Content: content}
			break
		}
		blocks = blocks[1:]
	}
	return blocks
}