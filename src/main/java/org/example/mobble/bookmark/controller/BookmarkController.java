package org.example.mobble.bookmark.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.mobble.bookmark.service.BookmarkService;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class BookmarkController {
    private final BookmarkService bookmarkService;
    private final HttpSession session;
}
