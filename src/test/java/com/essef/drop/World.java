package com.essef.drop;

import io.restassured.response.Response;

import java.util.ArrayList;
import java.util.List;

/** Shared per-scenario state: the last HTTP response plus anything the
 *  scenario created, so the After hook can clean it up even on failure. */
public class World {
    public Response last;
    public String lastNoteId;
    public String lastNoteText;
    public String lastUploadedName;
    public final List<String> createdNoteIds = new ArrayList<>();
    public final List<String> createdFileNames = new ArrayList<>();
}
