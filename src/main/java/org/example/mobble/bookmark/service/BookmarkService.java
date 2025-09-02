package org.example.mobble.bookmark.service;

import lombok.RequiredArgsConstructor;
import org.example.mobble.bookmark.domain.BookmarkRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;

}
