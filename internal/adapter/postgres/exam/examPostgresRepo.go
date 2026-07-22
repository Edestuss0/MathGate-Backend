package repo

import (
	"context"
	"database/sql"
	models "mathgate/internal/domain/exam"
	repo "mathgate/internal/domain/exam/repository"
)

type postgresRepository struct {
	db *sql.DB
}

func New(db *sql.DB) repo.ExamRepository {
	return &postgresRepository{db: db}
}


func (r *postgresRepository) GetRandomQuestion(examType string) (*models.ExamQuestion, error) {
	rows, err := r.db.Query(`SELECT 
			q.id, q.answer, q.type, q.theme_label, q.theme_number,
			b.id, b.content, b.type, b.position, b.section
		FROM (
			SELECT id, answer, type, theme_label, theme_number 
			FROM exam_questions 
			WHERE type = $1
			ORDER BY RANDOM() 
			LIMIT 1
		) q
		LEFT JOIN exam_blocks b ON q.id = b.question_id
		ORDER BY b.section ASC, b.position ASC`, examType)

		if err != nil {
			return nil, err
		}
		defer rows.Close()

		var question *models.ExamQuestion

		for rows.Next() {
			var (
				bId *int
				bContent *string
				bType *string
				bPosition *int
				bSection *string

				qType string
			)

			if question == nil {
				question = &models.ExamQuestion{
					Blocks: []models.ExamBlock{},
					SolutionBlocks: []models.ExamBlock{},
				}
				err = rows.Scan(
					&question.ID, &question.Answer, &qType, &question.ThemeLabel, &question.ThemeNumber,
					&bId, &bContent, &bType, &bPosition, &bSection,
				)
			} else {
				var dummyID, dummyThemeNum int
				var dummyAns, dummyLabel string
				err = rows.Scan(
					&dummyID, &dummyAns, &qType, &dummyLabel, &dummyThemeNum,
					&bId, &bContent, &bType, &bPosition, &bSection,
				)
			}

			if err != nil {
				return nil, err
			}

			if bId != nil {
				block := models.ExamBlock{
					BlockType: *bType,
					Content: *bContent,
				}
				if bSection != nil && (*bSection == "SOLUTION" || *bSection == "solution") {
					question.SolutionBlocks = append(question.SolutionBlocks, block)
				} else {
					question.Blocks = append(question.Blocks, block)
				}
			}
		}

		if err = rows.Err(); err != nil {
			return nil, err
		}

		if question == nil {
			return nil, sql.ErrNoRows
		}

		return question, nil
}

func (r *postgresRepository) GetQuestion(ctx context.Context, id int) (*models.ExamQuestion, error) {
	rows, err := r.db.QueryContext(ctx, `SELECT 
			q.id, q.answer, q.theme_label, q.theme_number,
			b.id, b.content, b.type, b.position, b.section
		FROM exam_questions q
		LEFT JOIN exam_blocks b ON q.id = b.question_id
		WHERE q.id = $1
		ORDER BY b.section ASC, b.position ASC`, id)

		if err != nil {
			return nil, err
		}
		defer rows.Close()

		var question *models.ExamQuestion

		for rows.Next() {
			var (
				bId *int
				bContent *string
				bType *string
				bPosition *int
				bSection *string

				qType string
			)

			if question == nil {
				question = &models.ExamQuestion{
					Blocks: []models.ExamBlock{},
					SolutionBlocks: []models.ExamBlock{},
				}
				err = rows.Scan(
					&question.ID, &question.Answer, &qType, &question.ThemeLabel, &question.ThemeNumber,
					&bId, &bContent, &bType, &bPosition, &bSection,
				)
			} else {
				var dummyID, dummyThemeNum int
				var dummyAns, dummyLabel string
				err = rows.Scan(
					&dummyID, &dummyAns, &qType, &dummyLabel, &dummyThemeNum,
					&bId, &bContent, &bType, &bPosition, &bSection,
				)
			}

			if err != nil {
				return nil, err
			}

			if bId != nil {
				block := models.ExamBlock{
					BlockType: *bType,
					Content: *bContent,
				}
				if bSection != nil && (*bSection == "SOLUTION" || *bSection == "solution") {
					question.SolutionBlocks = append(question.SolutionBlocks, block)
				} else {
					question.Blocks = append(question.Blocks, block)
				}
			}
		}

		if err = rows.Err(); err != nil {
			return nil, err
		}

		if question == nil {
			return nil, sql.ErrNoRows
		}

		return question, nil
}

func (r *postgresRepository) Save(ctx context.Context, question *models.ExamQuestion, examType string) error {
	tx, err := r.db.BeginTx(ctx, nil)
	if err != nil {
		return err
	}
	defer tx.Rollback()

	_, err = tx.ExecContext(ctx, `
	INSERT INTO exam_questions (id, answer, type, theme_label, theme_number)
	VALUES ($1, $2, $3, $4, $5)
	ON CONFLICT (id) DO NOTHING
	`, question.ID, question.Answer, examType, question.ThemeLabel, question.ThemeNumber)

	if err != nil {
			return err
	}

	stmt, err := tx.PrepareContext(ctx, `
		INSERT INTO exam_blocks (question_id, content, type, "position", section)
		VALUES ($1, $2, $3, $4, $5)
		`)
	if err != nil {
		return err
	}

	defer stmt.Close()

	for i, b := range question.Blocks {
		if _, err := stmt.ExecContext(ctx,  question.ID, b.Content, b.BlockType, i, "TASK"); err != nil {
			return err
		}
	}

	for i, b := range question.SolutionBlocks {
		if _, err := stmt.ExecContext(ctx,  question.ID, b.Content, b.BlockType, i, "SOLUTION"); err != nil {
			return err
		}
	}

	return tx.Commit()
		
}