package exam

type ExamQuestion struct {
	ID int `json:"id"`
	Answer string `json:"answer"`
	Blocks []ExamBlock `json:"blocks"`
	SolutionBlocks []ExamBlock `json:"solutionBlocks"`
	ThemeNumber int `json:"themeNumber"`
	ThemeLabel string `json:"themeLabel"`
}

type ExamBlock struct {
	BlockType string `json:"type"`
	Content string `json:"content"`
}

type ExamTheme struct {
	Number int `json:"number"`
	Label  string `json:"label"`
	Tasks  int `json:"tasks"`
}

type ThemeQuestion struct {
	TaskID      int
	ThemeNumber int
	ThemeLabel  string
}