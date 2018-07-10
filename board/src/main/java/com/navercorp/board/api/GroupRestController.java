package com.navercorp.board.api;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.navercorp.board.domain.Group;
import com.navercorp.board.exception.BadRequestException;
import com.navercorp.board.exception.GroupNotExistException;
import com.navercorp.board.exception.MismatchPatternException;
import com.navercorp.board.exception.NotEnoughDataException;
import com.navercorp.board.exception.NotExistException;
import com.navercorp.board.exception.UserNotExistException;
import com.navercorp.board.service.GroupService;

@RestController
@RequestMapping("/api/groups")
public class GroupRestController {

	@Autowired
	GroupService groupService;

	/**
	 * 필터링 rest api
	 * @param optionData 필터링 옵션을 포함한 객체
	 * @return 필터링 조건에 맞는 모임 리스트
	 */
	@PostMapping("filtering")
	private ResponseEntity<List<Group>> findGroups(@RequestBody Map<String, String> optionData) {
		try {
			List<Group> groups = groupService.getFilteringGroupList(optionData);
			return ResponseEntity.ok(groups);
		} catch (MismatchPatternException e) {
			throw new BadRequestException("잘못된 요청입니다.");
		}
	}

	/**
	 * 모임을 등록/수정하는 rest api (multipart는 post만 지원한다)
	 * @param group 모임 정보를 포함한 객체
	 * @param request 파일 정보를 포함한 객체
	 * @param session userId를 가져올 때 사용
	 * @return 모임이 생성되면 생성된 모임의 ID를 리턴, 이미지 등록 실패하면 500 error, 파일명이 틀리면 400 error 리턴
	 * @throws Exception 파일 업로드에 실패했을 때 Exception 발생
	 */
	@PostMapping
	private ResponseEntity<?> postGroup(@ModelAttribute Group group, HttpServletRequest request, HttpSession session) throws Exception {
		String category = group.getCategory();
		String recruitingPolicy = group.getRecruitingPolicy();
		LocalDateTime psDate = group.getPsDate();
		LocalDateTime pfDate = group.getPfDate();
		LocalDateTime rsDate = group.getRsDate();
		LocalDateTime rfDate = group.getRfDate();
		Integer personnel = group.getPersonnel(); // null 일 수 없다.
		String title = group.getTitle();
		String address = group.getAddress();
		String simpleInfo = group.getSimpleInfo(); //Group.imgAddr, Group.detailInfo는 nullable;
		
		if (category.length() == 0 || recruitingPolicy.length() == 0 || psDate == null || pfDate == null
				|| rsDate == null || rfDate == null || personnel == null || title.length() == 0
				|| address.length() == 0 || simpleInfo.length() == 0) {
			throw new NotEnoughDataException("데이터를 모두 입력해주세요.");
		}

		try {
			Long groupId = group.getGroupId();
			Long userId = (Long) session.getAttribute("userId");
			
			MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
			MultipartFile mFile = multiRequest.getFile("file");
			String originFileName = new String(mFile.getOriginalFilename());
			originFileName = originFileName.replace(" ", "_");
			
			Group storedGroup = null;
			if (groupId == null) { // 신규 등록 request
				storedGroup = groupService.create(mFile, group, userId, originFileName);
			} else { // 모임 수정 request
				storedGroup = groupService.update(mFile, group, userId, originFileName);
			}
			return ResponseEntity.ok(storedGroup.getGroupId());
		} catch (MismatchPatternException e) {
			throw new BadRequestException("입력을 다시 확인해주세요.");
		} catch (UserNotExistException | GroupNotExistException e) {
			throw new BadRequestException("잘못된 요청입니다.");
		} catch (BadRequestException e) {
			throw new BadRequestException("입력을 다시 확인해주세요.");
		} catch (IllegalStateException | IOException e) {
			throw new Exception();
		}
	}

	/**
	 * 모임을 삭제하는 rest api
	 * @param groupId 삭제할 모임 id
	 * @param request 세션을 가져오기 위한 객체
	 * @return 세션이 존재하지 않으면 UNAUTHORIZED, 그룹이 삭제되면 OK, 다른 오류로 그룹이 삭제되지 않으면
	 *         BAD_REQUEST와 각 메시지를 전달한다.
	 * @throws Exception 
	 */
	@DeleteMapping
	private ResponseEntity<?> deleteGroup(@RequestBody Properties groupData, HttpSession session) throws Exception {
		String groupIdString = groupData.getProperty("groupId", "");
		if (groupIdString.length() == 0) {
			throw new NotEnoughDataException("데이터가 충분하지 않습니다.");
		}
		
		try {
			Long userId = (Long) session.getAttribute("userId");
			Long groupId = Long.valueOf(groupIdString);
			groupService.deleteByGroupId(groupId, userId);
			return ResponseEntity.ok("모임이 삭제되었습니다.");
		} catch (NotExistException e) {
			throw new BadRequestException("존재하지 않는 모임이거나 권한이 없습니다.");
		} catch (IllegalStateException e) {
			throw new Exception();
		}
	}

}
