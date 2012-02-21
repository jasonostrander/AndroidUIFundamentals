package com.example.provider;


public class TaskStore {
    public static final String DEFAULT_SORT_ORDER = COLUMNS.DATE + "DESC";
    
    public static interface COLUMNS {
        public static final String NAME = "Name";
        public static final String DESCRIPTION = "Description";
        public static final String DATE = "Date";
    }
}
