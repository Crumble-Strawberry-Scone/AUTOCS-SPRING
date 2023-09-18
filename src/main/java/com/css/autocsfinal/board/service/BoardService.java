package com.css.autocsfinal.board.service;

import com.css.autocsfinal.board.dto.BoardDTO;
import com.css.autocsfinal.board.dto.BoardFileDTO;
import com.css.autocsfinal.board.entity.Board;
import com.css.autocsfinal.board.entity.BoardFile;
import com.css.autocsfinal.board.repository.BoardFileRepository;
import com.css.autocsfinal.board.repository.BoardRepository;
import com.css.autocsfinal.market.entity.StoreInfo2;
import com.css.autocsfinal.market.repository.StoreInfo2Repository;
import com.css.autocsfinal.member.entity.EmployeeAndDepartmentAndPosition;
import com.css.autocsfinal.member.entity.Member;
import com.css.autocsfinal.member.repository.EmployeeAndDepartmentAndPositionRepository;
import com.css.autocsfinal.member.repository.MemberRepository;
import com.css.autocsfinal.util.FileUploadUtils;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BoardService {

    private final BoardRepository boardRepository;
    private final ModelMapper modelMapper;
    private final EmployeeAndDepartmentAndPositionRepository employeeAndDepartmentAndPositionRepository;

    private final BoardFileRepository boardFileRepository;

    private final StoreInfo2Repository storeInfo2Repository;

    private final MemberRepository memberRepository;

    public BoardService(BoardRepository boardRepository, ModelMapper modelMapper, EmployeeAndDepartmentAndPositionRepository employeeAndDepartmentAndPositionRepository, BoardFileRepository boardFileRepository, StoreInfo2Repository storeInfo2Repository, MemberRepository memberRepository) {
        this.boardRepository = boardRepository;
        this.modelMapper = modelMapper;
        this.employeeAndDepartmentAndPositionRepository = employeeAndDepartmentAndPositionRepository;
        this.boardFileRepository = boardFileRepository;
        this.storeInfo2Repository = storeInfo2Repository;
        this.memberRepository = memberRepository;
    }

    @Value("${image.image-dir2}")
    private String IMAGE_DIR2;

    @Value("${image.image-url2}")
    private String IMAGE_URL2;



    //모든 게시물 조회
        public List<BoardDTO> getAllBoard() {
            log.info("[BoardService] 모든 게시물 조회 Start ===================");

            List<Board> boardList = boardRepository.findAll();
            log.info("boardList : " + boardList);

            List<BoardDTO> BoardDTOList = boardList.stream()
                    .map(board -> {
                        BoardDTO boardDTO = new BoardDTO();

                        boardDTO.setBoardNo(board.getBoardNo());
                        boardDTO.setRefCategoryNo(board.getRefCategoryNo());
                        boardDTO.setRegist(board.getRegist());
                        boardDTO.setTitle(board.getTitle());
                        boardDTO.setContent(board.getContent());
                        boardDTO.setRefMemberNo(board.getRefMemberNo());
                        boardDTO.setAnonymity(board.getAnonymity());

                        int memberNo = board.getRefMemberNo();

                        //멤버번호로 직원 조회
                        EmployeeAndDepartmentAndPosition employee = employeeAndDepartmentAndPositionRepository.findByMemberNo(memberNo);
                        if (employee != null) {
                            // 직원 정보가 존재하면 추가 정보 설정
                            boardDTO.setEmployeeName(employee.getName());
                            boardDTO.setDepartment(employee.getDepartment().getName());
                            boardDTO.setPosition(employee.getPosition().getName());
                        }

                        //멤버번호로 영업점 조회
                        StoreInfo2 store = storeInfo2Repository.findByMemberNo(memberNo);
                        if (store != null) {
                            // 영업점 정보가 존재하면 추가 정보 설정
                            boardDTO.setStoreName(store.getName());
                        }

                        return boardDTO;
                    })
                    .collect(Collectors.toList());

            return BoardDTOList;
        }

    //게시물 등록(파일 있을 경우)
    @Transactional
    public String insertboardFile(BoardDTO boardDTO, MultipartFile[] fileImages) {
        log.info("[BoardService] 게시물 Insert Start ===================");
        log.info("[BoardService] boardDTO {} =======> " + boardDTO);
        log.info("[BoardService] fileImages {} =======> " + fileImages);

        Board insertBoard = modelMapper.map(boardDTO, Board.class);
        insertBoard.setRegist(Date.valueOf(LocalDate.now()));

        // ApplyFormAndApplyFile 객체 생성 및 매핑
        Board boardFrom = boardRepository.save(insertBoard);

        List<BoardFile> savedFiles = new ArrayList<>();
        try {
            for (MultipartFile fileImage : fileImages) {
                String imageName = UUID.randomUUID().toString().replace("-", "");
                String replaceFileName = FileUploadUtils.saveFile(IMAGE_DIR2, imageName, fileImage);

                log.info("[BoardService] insert Image Name : {}", replaceFileName);

                BoardFileDTO boardFileDTO = new BoardFileDTO();

                // ApplyFile 엔티티로 매핑하고 저장
                BoardFile insertFile = modelMapper.map(boardFileDTO, BoardFile.class);
                insertFile.setOriginal(replaceFileName);
                insertFile.setChange("게시물" + boardDTO.getRefMemberNo());
                insertFile.setRegist(Date.valueOf(LocalDate.now()));

                Integer maxBoardNo = boardRepository.findMaxBoardNo();
                insertFile.setRefBoardNo(maxBoardNo);
                insertFile.setDropYN('N');

                BoardFile savedFile = boardFileRepository.save(insertFile);
                savedFiles.add(savedFile);
            }

            log.info("[BoardService] 게시물 Insert End ===================");
            return (boardFrom != null && !savedFiles.isEmpty()) ? "게시물 등록 성공" : "게시물 등록 실패";
        } catch (Exception e) {
            log.error("Error while inserting apply form: {}", e.getMessage());
            for (BoardFile savedFile : savedFiles) {
                FileUploadUtils.deleteFile(IMAGE_DIR2, savedFile.getOriginal());
            }
            throw new RuntimeException(e);
        }
    }

    //게시물 등록(파일 없을 경우)
    @Transactional
    public String insertboardNotFile(BoardDTO boardDTO) {
        log.info("[BoardService] 게시물 Insert Start ===================");
        log.info("[BoardService] boardDTO {} =======> " + boardDTO);

        try {
            Board insertBoard = modelMapper.map(boardDTO, Board.class);
            insertBoard.setRegist(Date.valueOf(LocalDate.now()));

            Board boardFrom = boardRepository.save(insertBoard);

            log.info("[BoardService] 게시물 Insert End ===================");
            return (boardFrom != null) ? "게시물 등록 성공" : "게시물 등록 실패";
        } catch (Exception e) {
            log.error("Error while inserting board: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //게시물 수정(파일 있을 경우)
    @Transactional
    public String updateBoardFile(BoardDTO boardDTO, MultipartFile[] fileImages) {
        try {
            log.info("[BoardService] 게시물 Update Start ===================");
            log.info("[BoardService] boardDTO {} =======> {}", boardDTO);
            log.info("[BoardService] fileImages {} =======> {}", fileImages);

            // 1. 먼저 게시물 찾아야함
            Board board = boardRepository.findByBoardNo(boardDTO.getBoardNo());
            log.info("board>>>>>>>>>>>>>>>>>> {}", board);

            if (board == null) {
                return "게시물을 찾을 수 없습니다.";
            }

            board.setTitle(boardDTO.getTitle());
            board.setContent(boardDTO.getContent());
            board.setModify(Date.valueOf(LocalDate.now()));
            board.setRefCategoryNo(boardDTO.getRefCategoryNo());
            board.setAnonymity(boardDTO.getAnonymity());

            Board boardFrom = boardRepository.save(board);

            // 2. 파일 먼저 삭제
            int refBoardNo = boardDTO.getBoardNo();
            List<BoardFile> boardFiles = boardFileRepository.findByRefBoardNo(refBoardNo);

            if (boardFiles != null && !boardFiles.isEmpty()) {
                for (BoardFile boardFile : boardFiles) {
                    String originalFileName = boardFile.getOriginal();
                    File file = new File(IMAGE_DIR2, originalFileName);

                    // 파일이 존재하면 삭제
                    if (file.exists()) {
                        file.delete();
                    }

                    boardFileRepository.delete(boardFile);
                }
            }

            // 3. 파일 다시 insert
            List<BoardFile> savedFiles = new ArrayList<>();
            for (MultipartFile fileImage : fileImages) {
                String imageName = UUID.randomUUID().toString().replace("-", "");
                String replaceFileName = FileUploadUtils.saveFile(IMAGE_DIR2, imageName, fileImage);

                log.info("[BoardService] insert Image Name : {}", replaceFileName);

                BoardFile insertFile = new BoardFile();
                insertFile.setOriginal(replaceFileName);
                insertFile.setChange("게시물" + boardDTO.getRefMemberNo());
                insertFile.setRegist(Date.valueOf(LocalDate.now()));

                Integer maxBoardNo = boardRepository.findMaxBoardNo();
                insertFile.setRefBoardNo(maxBoardNo);
                insertFile.setDropYN('N');

                BoardFile savedFile = boardFileRepository.save(insertFile);
                savedFiles.add(savedFile);
            }

            log.info("[BoardService] 게시물 수정 성공");
            return "게시물 수정 성공";

        } catch (Exception e) {
            log.error("게시물 수정 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("게시물 수정 실패");
        }
    }

    //게시물 수정(파일 없을 경우)
    @Transactional
    public String updateBoardNotFile(BoardDTO boardDTO) {
        log.info("[BoardService] 게시물 Update Start ===================");
        log.info("[BoardService] boardDTO {} =======> " + boardDTO);

        try {
            // 1. 먼저 게시물 찾아야함
            Board board = boardRepository.findByBoardNo(boardDTO.getBoardNo());
            log.info("board>>>>>>>>>>>>>>>>>> {}", board);

            if (board == null) {
                return "게시물을 찾을 수 없습니다.";
            }

            board.setTitle(boardDTO.getTitle());
            board.setContent(boardDTO.getContent());
            board.setModify(Date.valueOf(LocalDate.now()));
            board.setRefCategoryNo(boardDTO.getRefCategoryNo());
            board.setAnonymity(boardDTO.getAnonymity());

            Board boardFrom = boardRepository.save(board);

            log.info("[BoardService] 게시물 Update End ===================");
            return (boardFrom != null) ? "게시물 등록 성공" : "게시물 등록 실패";
        } catch (Exception e) {
            log.error("Error while inserting board: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //게시물 조회
    public BoardDTO finBoard(int boardNo) {
        Board board = boardRepository.findByBoardNo(boardNo);
        log.info("board : " + board);

        // BoardDTO에 정보를 담아서 반환
        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setBoardNo(boardNo);
        boardDTO.setTitle(board.getTitle());
        boardDTO.setRefCategoryNo(board.getRefCategoryNo());
        boardDTO.setRegist(board.getRegist());
        boardDTO.setContent(board.getContent());
        boardDTO.setAnonymity(board.getAnonymity());

        int no = board.getRefMemberNo();

        //멤버 번호로 권한 담기
        Member member = memberRepository.findByNo(no);
        boardDTO.setMemnerRole(member.getRole());

        int memberNo = board.getRefMemberNo();

        //멤버번호로 직원 조회
        EmployeeAndDepartmentAndPosition employee = employeeAndDepartmentAndPositionRepository.findByMemberNo(memberNo);
        if (employee != null) {
            // 직원 정보가 존재하면 추가 정보 설정
            boardDTO.setEmployeeName(employee.getName());
            boardDTO.setDepartment(employee.getDepartment().getName());
            boardDTO.setPosition(employee.getPosition().getName());
            boardDTO.setRefMemberNo(memberNo);
        }

        //멤버번호로 영업점 조회
        StoreInfo2 store = storeInfo2Repository.findByMemberNo(memberNo);
        if (store != null) {
            // 영업점 정보가 존재하면 추가 정보 설정
            boardDTO.setStoreName(store.getName());
            boardDTO.setRefMemberNo(memberNo);
        }

        // 파일 조회
        int refBoardNo = boardNo;
        List<BoardFile> fileList = boardFileRepository.findByRefBoardNo(refBoardNo);
        List<String> fileUrls = new ArrayList<>();

        for (BoardFile file : fileList) {
            fileUrls.add(IMAGE_URL2 + file.getOriginal());
        }

        boardDTO.setFileUrls(fileUrls);

        return boardDTO;
    }

    //게시물 삭제
    @Transactional
    public String deleteBoard(int boardNo) {
        log.info("[BoardService] 게시물 Delete Start ===================");
        try {
            int deletedRowCount = boardRepository.deleteByBoardNo(boardNo);

            int refBoardNo = boardNo;
            List<BoardFile> boardFiles = boardFileRepository.findByRefBoardNo(refBoardNo);

            if (deletedRowCount > 0) {
                if (boardFiles != null && !boardFiles.isEmpty()) {
                    for (BoardFile boardFile : boardFiles) {
                        boardFileRepository.delete(boardFile);
                    }
                    log.info("[BoardService] 게시물 Delete End ===================");
                    return "게시물 및 파일들 삭제 성공";
                } else {
                    log.info("[BoardService] 게시물 Delete End ===================");
                    return "게시물 삭제 성공 (파일은 없음)";
                }
            } else {
                return "게시물 삭제 실패";
            }
        } catch (Exception e) {
            log.error("게시물 삭제 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // 게시물 조회(내가 쓴 글)
    public List<BoardDTO> findMyBoardEmployee(int refMemberNo) {
        log.info("[BoardService] refMemberNo {} =======> " + refMemberNo);
        List<Board> boardList = boardRepository.findByRefMemberNo(refMemberNo);
        List<BoardDTO> boardDTOList = new ArrayList<>(); // 결과를 담을 리스트 생성

        for (Board board : boardList) {
            BoardDTO boardDTO = new BoardDTO();
            boardDTO.setBoardNo(board.getBoardNo());
            boardDTO.setTitle(board.getTitle());
            boardDTO.setRefCategoryNo(board.getRefCategoryNo());
            boardDTO.setRegist(board.getRegist());
            boardDTO.setContent(board.getContent());
            boardDTO.setAnonymity(board.getAnonymity());

            int no = board.getRefMemberNo();

            // 멤버 번호로 권한 담기
            Member member = memberRepository.findByNo(no);
            boardDTO.setMemnerRole(member.getRole());

            int memberNo = board.getRefMemberNo();

            // 멤버번호로 직원 조회
            EmployeeAndDepartmentAndPosition employee = employeeAndDepartmentAndPositionRepository.findByMemberNo(memberNo);
            if (employee != null) {
                // 직원 정보가 존재하면 추가 정보 설정
                boardDTO.setEmployeeName(employee.getName());
                boardDTO.setDepartment(employee.getDepartment().getName());
                boardDTO.setPosition(employee.getPosition().getName());
                boardDTO.setRefMemberNo(memberNo);
            }

            // 멤버번호로 영업점 조회
            StoreInfo2 store = storeInfo2Repository.findByMemberNo(memberNo);
            if (store != null) {
                // 영업점 정보가 존재하면 추가 정보 설정
                boardDTO.setStoreName(store.getName());
                boardDTO.setRefMemberNo(memberNo);
            }

            boardDTOList.add(boardDTO); // 결과 리스트에 추가
        }

        return boardDTOList;
    }
}

