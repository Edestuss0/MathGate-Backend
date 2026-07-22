package storage

func (s *Storage) initSchema() error {
	if err := s.initQuestionsStorage(); err != nil {
		return err
	}
	return nil
}

func (s *Storage) initQuestionsStorage() error {
	query := `
	CREATE TABLE IF NOT EXISTS exam_questions (
		id INTEGER PRIMARY KEY,
		answer TEXT NOT NULL,
		type VARCHAR(255) NOT NULL,
		theme_label VARCHAR(255) NOT NULL,
		theme_number INTEGER NOT NULL
	);

	-- 2. Затем создаем зависимую таблицу блоков
	CREATE TABLE IF NOT EXISTS exam_blocks (
		id SERIAL PRIMARY KEY,
		question_id INTEGER NOT NULL,
		content TEXT NOT NULL,
		type VARCHAR(255) NOT NULL,
		"position" INTEGER DEFAULT 0 NOT NULL,
		section VARCHAR(255) NOT NULL,
		
		CONSTRAINT fk_exam_blocks_question 
			FOREIGN KEY (question_id) 
			REFERENCES exam_questions(id) 
			ON DELETE CASCADE
	);

	-- 3. Создаем индекс для оптимизации сортировки @OrderBy("section ASC, position ASC")
	CREATE INDEX IF NOT EXISTS idx_exam_blocks_order 
	ON exam_blocks (question_id, section ASC, "position" ASC);
	`

	if _, err := s.DB.Exec(query); err != nil {
		return err
	}

	return nil
}
