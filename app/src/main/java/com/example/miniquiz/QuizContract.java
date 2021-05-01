package com.example.miniquiz;

import android.provider.BaseColumns;

public final class QuizContract {

    private QuizContract() {
    }
    public static class SubjectTable implements BaseColumns{
        public static final String TABLE_NAME = "quiz_subjects";
        public static final String COLUMN_NAME = "name";
    }

    public static class QuestionsTable implements BaseColumns {
        public static final String TABLE_NAME = "quiz_questions";
        public static final String COLUMN_QUESTION = "question";
        public static final String COLUMN_OPTION_1 = "option1";
        public static final String COLUMN_OPTION_2 = "option2";
        public static final String COLUMN_OPTION_3 = "option3";
        public static final String COLUMN_OPTION_4 = "option4";
        public static final String COLUMN_ANSWER_NR = "answer_nr";
        public static final String COLUMN_DIFFICULTY = "difficulty";
        public static final String COLUMN_SUBJECT_ID = "category_id";

    }

}
