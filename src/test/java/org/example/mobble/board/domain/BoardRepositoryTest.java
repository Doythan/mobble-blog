package org.example.mobble.board.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

@Import(BoardRepository.class)
@DataJpaTest
public class BoardRepositoryTest {

    @Autowired
    private BoardRepository boardRepository;

    @Test
    public void findboardList_test() {
        List<Board> foundBoardList = boardRepository.findboardList();
        for (Board board : foundBoardList) {
            System.out.println(board.getId());
            System.out.println(board.getTitle());
            System.out.println(board.getContent());
            System.out.println(board.getUser().getUsername());
            System.out.println(board.getCategory().getCategory());
            System.out.println(board.getCreatedAt());
        }
    }
}
