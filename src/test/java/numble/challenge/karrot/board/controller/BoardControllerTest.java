package numble.challenge.karrot.board.controller;

import numble.challenge.karrot.board.entity.Board;
import numble.challenge.karrot.board.repository.BoardRepository;
import numble.challenge.karrot.board.utils.BoardStatus;
import numble.challenge.karrot.board.utils.Category;
import numble.challenge.karrot.common.session.SessionConst;
import numble.challenge.karrot.interest.entity.Interest;
import numble.challenge.karrot.interest.repository.InterestRepository;
import numble.challenge.karrot.login.form.LoginMemberForm;
import numble.challenge.karrot.member.entity.Member;
import numble.challenge.karrot.member.repository.MemberRepository;
import numble.challenge.karrot.member.utils.MemberStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@DisplayName("BoardController ?????????")
public class BoardControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private BoardRepository boardRepository;

    @MockBean
    private InterestRepository interestRepository;

    private Board board;
    private Member member;
    private LoginMemberForm loginMemberForm;
    private List<Board> boardList;
    private MockHttpSession session;
    private MultiValueMap<String, String> params;
    private List<Interest> interestList;

    @BeforeEach
    void beforeEach() {
        this.member = Member.builder()
                .id(1L)
                .email("test@user.com")
                .password("1234")
                .name("a")
                .phoneNumber("01012345678")
                .nickname("a")
                .profile(null)
                .status(MemberStatus.????????????)
                .uuid(UUID.randomUUID().toString())
                .place("????????? ?????????")
                .build();

        Board board = Board.builder()
                .id(1L)
                .title("????????? 1")
                .place("????????? ?????????")
                .price(0)
                .status(BoardStatus.?????????)
                .category(Category.C0)
                .content("????????? ??????")
                .thumbnail("????????? 1")
                .interestCount(0)
                .replyCount(0)
                .member(this.member)
                .images(new ArrayList<>())
                .build();

        board.setCreatedDate(LocalDateTime.now());

        this.board = board;

        this.loginMemberForm = LoginMemberForm.builder()
                .id(1L)
                .email("test@user.com")
                .nickname("a")
                .profile(null)
                .status(MemberStatus.????????????)
                .build();

        this.boardList = List.of(
                Board.builder()
                        .id(1L)
                        .title("????????? 1")
                        .place("????????? ?????????")
                        .price(0)
                        .status(BoardStatus.?????????)
                        .category(Category.C0)
                        .content("????????? ??????")
                        .thumbnail("????????? 1")
                        .interestCount(0)
                        .replyCount(0)
                        .images(new ArrayList<>())
                        .build(),

                Board.builder()
                        .id(2L)
                        .title("????????? 2")
                        .place("????????? ?????????")
                        .price(0)
                        .status(BoardStatus.?????????)
                        .category(Category.C0)
                        .content("????????? ??????")
                        .thumbnail("????????? 2")
                        .interestCount(0)
                        .replyCount(0)
                        .images(new ArrayList<>())
                        .build()
        );

        this.loginMemberForm = LoginMemberForm.builder()
                .id(1L)
                .email("test@user.com")
                .nickname("a")
                .profile(null)
                .status(MemberStatus.????????????)
                .verifyDate(LocalDateTime.now())
                .build();

        this.session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMemberForm);

        this.params = new LinkedMultiValueMap<>();

        this.interestList = List.of(
                Interest.builder()
                        .id(1L)
                        .board(board)
                        .member(member)
                        .build()
        );
    }

    @Nested
    @DisplayName("GET:/boards/new")
    class GetBoardFormTest {
        @Test
        @DisplayName("?????? ?????????")
        void getBoardFormSuccessTest() throws Exception {
            mvc.perform(MockMvcRequestBuilders.get("/boards/new").session(session))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("text/html;charset=UTF-8"))
                    .andExpect(model().attributeExists("addBoardForm"))
                    .andExpect(view().name("board/add-board-form"));
        }

        @Test
        @DisplayName("?????? ????????? - ????????? ?????? ??????")
        void getBoardFormFailTest() throws Exception {
            mvc.perform(MockMvcRequestBuilders.get("/boards/new"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string("Location", "/login?redirectURL=/boards/new"));
        }
    }

    @Nested
    @DisplayName("POST:/boards/new")
    class AddBoardTest {
        @Test
        @DisplayName("?????? ?????????")
        void addBoardSuccessTest() throws Exception {
            doReturn(Optional.of(member)).when(memberRepository).findById(anyLong());
            doReturn(board).when(boardRepository).save(any(Board.class));

            params.add("title", "????????? 1");
            params.add("price", "0");
            params.add("content", "????????? ??????");
            params.add("category", Category.C0.toString());

            mvc.perform(MockMvcRequestBuilders.post("/boards/new").session(session).params(params))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string("Location", "/boards/1/my-detail"));
        }

        @Test
        @DisplayName("?????? ????????? - ????????? ?????? ??????")
        void addBoardNotLoginTest() throws Exception {
            mvc.perform(MockMvcRequestBuilders.get("/boards/new"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string("Location", "/login?redirectURL=/boards/new"));
        }

        @Test
        @DisplayName("?????? ????????? - ?????? ??????")
        void addBoardEmptyValueTest() throws Exception {
            doReturn(Optional.of(member)).when(memberRepository).findById(anyLong());
            doReturn(board).when(boardRepository).save(any(Board.class));

            mvc.perform(MockMvcRequestBuilders.post("/boards/new").session(session))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeHasFieldErrors("addBoardForm", "title", "content", "category"))
                    .andExpect(view().name("board/add-board-form"));
        }

        @Test
        @DisplayName("?????? ????????? - ?????? ?????? ??????")
        void addBoardPriceMinusValueTest() throws Exception {
            doReturn(Optional.of(member)).when(memberRepository).findById(anyLong());
            doReturn(board).when(boardRepository).save(any(Board.class));

            params.add("title", "????????? 1");
            params.add("price", "-1");
            params.add("content", "????????? ??????");
            params.add("category", Category.C0.toString());

            mvc.perform(MockMvcRequestBuilders.post("/boards/new").session(session).params(params))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeHasFieldErrors("addBoardForm", "price"))
                    .andExpect(view().name("board/add-board-form"));
        }
    }

    @Nested
    @DisplayName("GET:/boards/{id}")
    class GetBoardDetailTest {
        @Test
        @DisplayName("?????? ?????????")
        void getBoardDetailSuccessTest() throws Exception {
            doReturn(Optional.of(member)).when(memberRepository).findById(anyLong());
            doReturn(Optional.of(board)).when(boardRepository).findById(anyLong());
            doReturn(interestList).when(interestRepository).findAllByMemberId(anyLong());

            mvc.perform(MockMvcRequestBuilders.get("/boards/{id}", 1).session(session).header("Referer", "/"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string("Location", "/boards/1/my-detail"));
        }

        @Test
        @DisplayName("?????? ????????? - ????????? ?????? ??????")
        void getBoardDetailFailNotLoginTest() throws Exception {
            mvc.perform(MockMvcRequestBuilders.get("/boards/{id}", 1))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string("Location", "/login?redirectURL=/boards/1"));
        }

        @Test
        @DisplayName("?????? ????????? - ???????????? ?????? ????????? ID")
        void getBoardDetailFailNotBoardIdTest() throws Exception {
            doReturn(Optional.ofNullable(null)).when(boardRepository).findById(anyLong());

            mvc.perform(MockMvcRequestBuilders.get("/boards/{id}", 1).session(session))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("text/html;charset=UTF-8"))
                    .andExpect(model().attribute("message", "???????????? ?????? ??????????????????."))
                    .andExpect(view().name("error/exception-page"));
        }
    }

    @Nested
    @DisplayName("GET:/boards/{id}/other")
    class memberIdOtherBoardListTest {
        @Test
        @DisplayName("?????? ?????????")
        void memberIdOtherBoardListSuccessTest() throws Exception {
            doReturn(Optional.of(board)).when(boardRepository).findById(anyLong());
            doReturn(boardList).when(boardRepository).findAllByMemberId(anyLong());

            mvc.perform(MockMvcRequestBuilders.get("/boards/{id}/other", 1).session(session))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("text/html;charset=UTF-8"))
                    .andExpect(model().attributeExists("memberId", "boardId", "list"))
                    .andExpect(view().name("board/other-board-list"));
        }

        @Test
        @DisplayName("?????? ????????? - ????????? ?????? ??????")
        void memberIdOtherBoardListFailNotLoginTest() throws Exception {
            mvc.perform(MockMvcRequestBuilders.get("/boards/{id}/other", 1))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string("Location", "/login?redirectURL=/boards/1/other"));
        }
    }

    @Nested
    @DisplayName("GET:/boards/{id}/my-detail")
    class MyDetailBoardFormTest {
        @Test
        @DisplayName("?????? ?????????")
        void myDetailBoardFormSuccessTest() throws Exception {
            doReturn(Optional.of(member)).when(memberRepository).findById(anyLong());
            doReturn(Optional.of(board)).when(boardRepository).findById(anyLong());
            doReturn(interestList).when(interestRepository).findAllByMemberId(anyLong());

            mvc.perform(MockMvcRequestBuilders.get("/boards/{id}/my-detail", 1).session(session))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("text/html;charset=UTF-8"))
                    .andExpect(model().attributeExists("form", "type"))
                    .andExpect(view().name("board/update-board-detail"));

        }

        @Test
        @DisplayName("?????? ????????? - ???????????? ?????? ????????? ID")
        void myDetailBoardFormFailNotBoardIdTest() throws Exception {
            doReturn(Optional.ofNullable(null)).when(boardRepository).findById(anyLong());

            mvc.perform(MockMvcRequestBuilders.get("/boards/{id}/my-detail", 1).session(session))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("text/html;charset=UTF-8"))
                    .andExpect(model().attribute("message", "???????????? ?????? ??????????????????."))
                    .andExpect(view().name("error/exception-page"));
        }

        @Test
        @DisplayName("?????? ????????? - ???????????? ?????? ?????? ID")
        void myDetailBoardFormFailNotMemberIdTest() throws Exception {
            doReturn(Optional.of(board)).when(boardRepository).findById(anyLong());
            doReturn(Optional.ofNullable(null)).when(memberRepository).findById(anyLong());

            mvc.perform(MockMvcRequestBuilders.get("/boards/{id}/my-detail", 1).session(session))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("text/html;charset=UTF-8"))
                    .andExpect(model().attribute("message", "???????????? ?????? ?????? ??????????????????."))
                    .andExpect(view().name("error/exception-page"));
        }

        @Test
        @DisplayName("?????? ????????? - ????????? ?????? ??????")
        void getBoardDetailFailNotLoginTest() throws Exception {
            mvc.perform(MockMvcRequestBuilders.get("/boards/{id}/my-detail", 1))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string("Location", "/login?redirectURL=/boards/1/my-detail"));
        }
    }

    @Nested
    @DisplayName("GET:/boards/{id}/my-update")
    class MyUpdateBoardFormTest {
        @Test
        @DisplayName("?????? ?????????")
        void myUpdateBoardFormSuccessTest() throws Exception {
            doReturn(Optional.of(board)).when(boardRepository).findById(anyLong());

            mvc.perform(MockMvcRequestBuilders.get("/boards/{id}/my-update", 1).session(session))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("text/html;charset=UTF-8"))
                    .andExpect(model().attributeExists("updateBoardForm", "category"))
                    .andExpect(view().name("board/board-update"));
        }

        @Test
        @DisplayName("?????? ????????? - ???????????? ?????? ????????? ID")
        void myUpdateBoardFormFailNotMemberIdTest() throws Exception {
            doReturn(Optional.ofNullable(null)).when(boardRepository).findById(anyLong());

            mvc.perform(MockMvcRequestBuilders.get("/boards/{id}/my-update", 1).session(session))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("text/html;charset=UTF-8"))
                    .andExpect(model().attribute("message", "???????????? ?????? ??????????????????."))
                    .andExpect(view().name("error/exception-page"));
        }

        @Test
        @DisplayName("?????? ????????? - ????????? ?????? ??????")
        void myUpdateBoardFormFailNotLoginTest() throws Exception {
            mvc.perform(MockMvcRequestBuilders.get("/boards/{id}/my-update", 1))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string("Location", "/login?redirectURL=/boards/1/my-update"));
        }
    }

    @Nested
    @DisplayName("POST:/boards/{id}/my-update")
    class MyUpdateBoardTest {
        @Test
        @DisplayName("?????? ?????????")
        void myUpdateBoardSuccessTest() throws Exception {
            doReturn(Optional.of(board)).when(boardRepository).findById(anyLong());

            params.add("title", "????????? ?????? ??????");
            params.add("price", "1");
            params.add("content", "????????? ?????? ??????");
            params.add("category", Category.C2.toString());

            mvc.perform(MockMvcRequestBuilders.post("/boards/{id}/my-update", 1).session(session).params(params))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string("Location", "/boards/1/my-detail"));
        }

        @Test
        @DisplayName("?????? ????????? - ?????? ??????")
        void myUpdateBoardFailEmptyValueTest() throws Exception {
            doReturn(Optional.of(board)).when(boardRepository).findById(anyLong());

            mvc.perform(MockMvcRequestBuilders.post("/boards/{id}/my-update", 1).session(session))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeHasFieldErrors("updateBoardForm", "title", "category", "content"))
                    .andExpect(view().name("board/board-update"));
        }

        @Test
        @DisplayName("?????? ????????? - ???????????? ?????? ????????? ID")
        void myUpdateBoardFailNotBoardIdTest() throws Exception {
            doReturn(Optional.ofNullable(null)).when(boardRepository).findById(anyLong());

            params.add("title", "????????? ?????? ??????");
            params.add("price", "1");
            params.add("content", "????????? ?????? ??????");
            params.add("category", Category.C2.toString());

            mvc.perform(MockMvcRequestBuilders.post("/boards/{id}/my-update", -1).session(session).params(params))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("text/html;charset=UTF-8"))
                    .andExpect(model().attribute("message", "???????????? ?????? ??????????????????."))
                    .andExpect(view().name("error/exception-page"));
        }

        @Test
        @DisplayName("?????? ????????? - ????????? ?????? ??????")
        void myUpdateBoardFormFailNotLoginTest() throws Exception {
            mvc.perform(MockMvcRequestBuilders.post("/boards/{id}/my-update", 1))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string("Location", "/login?redirectURL=/boards/1/my-update"));
        }
    }

    @Nested
    @DisplayName("GET:/boards/interest")
    class InterestBoardTest {
        @Test
        @DisplayName("?????? ?????????")
        void interestBoardSuccessTest() throws Exception {
            doReturn(interestList).when(interestRepository).findAllByMemberId(anyLong());
            doReturn(Optional.of(member)).when(memberRepository).findById(anyLong());

            mvc.perform(MockMvcRequestBuilders.get("/boards/interest").session(session))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("text/html;charset=UTF-8"))
                    .andExpect(model().attributeExists("list"))
                    .andExpect(view().name("board/interest-board-list"));
        }

        @Test
        @DisplayName("?????? ????????? - ????????? ?????? ??????")
        void interestBoardFailNotLoginTest() throws Exception {
            mvc.perform(MockMvcRequestBuilders.get("/boards/interest"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string("Location", "/login?redirectURL=/boards/interest"));
        }

        @Test
        @DisplayName("?????? ????????? - ???????????? ?????? ?????? ID")
        void interestBoardFailNotMemberIdIdTest() throws Exception {
            doReturn(Optional.of(board)).when(boardRepository).findById(anyLong());
            doReturn(Optional.ofNullable(null)).when(memberRepository).findById(anyLong());

            mvc.perform(MockMvcRequestBuilders.get("/boards/interest").session(session))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("text/html;charset=UTF-8"))
                    .andExpect(model().attribute("message", "???????????? ?????? ?????? ??????????????????."))
                    .andExpect(view().name("error/exception-page"));
        }
    }
}
