package com.navercorp.board.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.navercorp.board.BoardApplication;
import com.navercorp.board.constant.Const;
import com.navercorp.board.constant.Utils;
import com.navercorp.board.domain.Group;
import com.navercorp.board.domain.User;
import com.navercorp.board.exception.MismatchPatternException;
import com.navercorp.board.exception.NotExistException;
import com.navercorp.board.exception.UserNotExistException;
import com.navercorp.board.repository.GroupRepository;
import com.navercorp.board.repository.UserRepository;

@Service
public class GroupService {

	@Autowired
	private GroupRepository groupRepository;
	
	@Autowired
	private UserRepository userRepository;

	public List<Group> findAll() {
		return groupRepository.findAllAppliableGroups(LocalDateTime.now());
	}

	public Group findByGroupId(Long groupId) {
		return groupRepository.findByGroupId(groupId);
	}

	public Group findByIdAndUserId(Long groupId, User user) {
		return groupRepository.findByGroupIdAndUser(groupId, user);
	}

	/**
	 * 입력받은 키워드로 모임의 title, simpleInfo를 조회해 group list를 반환한다.
	 * @param keyword 검색어
	 * @return title과 simpleInfo에 keyword가 포함된 group list
	 */
	public List<Group> findByKeyword(String keyword) {
		return groupRepository.findGroupsBySearchKeyword(keyword);
	}

	/**
	 * 필터링 옵션에 맞는 모임을 찾는다.
	 * @param options 필터링 옵션
	 * @return 필터링 옵션에 맞는 모임 목록
	 * @throws MismatchPatternException 꼭 필요한 옵션이 없는 경우
	 */
	public List<Group> getFilteringGroupList(Map<String, String> options) throws MismatchPatternException {
		String keyword = options.getOrDefault("keyword", "");
		String category = options.getOrDefault("category", "");
		String address = options.getOrDefault("address", "");
		String weekdate = options.getOrDefault("weekdate", "");
		String time = options.getOrDefault("time", "");
		
		// keyword, weekdate는 빈 문자열이어도 상관 없다.
		boolean categoryCheck = category.equals("");
		boolean addressCheck = category.equals("");
		boolean timeCheck = category.equals("");
		
		// 나머지는 빈문자열이어서는 안된다.
		if ( categoryCheck || addressCheck || timeCheck ) {
			throw new MismatchPatternException();
		}
		
		int weekdateStart = 0;
		int weekdateEnd = 6;

		if (weekdate.indexOf(Const.WEEKDAY) >= 0) {
			weekdateStart = 0;
			weekdateStart = 4;
		} else if (weekdate.indexOf(Const.WEEKEND) >= 0){
			weekdateStart = 5;
			weekdateEnd = 6;
		} else {
			weekdateStart = 0;
			weekdateEnd = 6;
		}
		
		if (keyword.length() == 0) { // 키워드에 따라 다른 쿼리를 실행한다.
			return groupRepository.findGroupsByFiltering(category, address, time, weekdateStart, weekdateEnd);
		} else {
			return groupRepository.findGroupsByFilteringWithKeyword(keyword, category, address, time, weekdateStart, weekdateEnd);
		}
		
	}

	/**
	 * 모임을 생성한다.
	 * @param mFile 멀티파트 파일
	 * @param group 모임 정보
	 * @param userId 작성자 아이디
	 * @param fileName 업르도 파일 명
	 * @return
	 * @throws MismatchPatternException 필요한 정보가 없는 경우 발생
	 * @throws UserNotExistException 작성자가 존재하지 않는 경우 발생
	 * @throws IllegalStateException 파일명이 올바르지 않은 경우 발생
	 * @throws IOException 파일 쓰기가 실패한 경우 발생
	 */
	@Transactional
	public Group create(MultipartFile mFile, Group group, Long userId, String fileName) throws MismatchPatternException,
																							   UserNotExistException,
																							   IllegalStateException,
																							   IOException {
		boolean titleCheck = Pattern.matches(".{5,100}", group.getTitle());
		boolean recruitingPolicyCheck = Pattern.matches("^FCFS$|^SELECTION$", group.getRecruitingPolicy());
		boolean pDateCheck = group.getPsDate().isBefore(group.getPfDate());
		boolean rDateCheck = group.getRsDate().isBefore(group.getRfDate());
		boolean prDateCheck = (group.getRfDate().isEqual(group.getPsDate()) 
								|| group.getRfDate().isBefore(group.getPsDate()));
		boolean personnelCheck = (0 < group.getPersonnel() && group.getPersonnel() <= 10000);
		boolean categoryCheck = Pattern.matches("^교육$|^강연$|^취미활동$|^여행$", group.getCategory());
		boolean addressCheck = Pattern.matches(".{5,100}", group.getAddress());
		boolean simpleInfoCheck = Pattern.matches(".{10,100}", group.getSimpleInfo());
		boolean detailInfoCheck = Pattern.matches(".{0,2000}", group.getDetailInfo());

		if ( !(titleCheck && recruitingPolicyCheck && pDateCheck && rDateCheck && prDateCheck && personnelCheck
				&& categoryCheck && addressCheck && addressCheck && simpleInfoCheck && detailInfoCheck)) {
			throw new MismatchPatternException();
		}
		
		String address = group.getAddress();
		String[] addressArray = address.split(" ");
		String addressDo = addressArray[0];
		group.setAddressDo(addressDo);
		

		User storedUser =userRepository.findByUserId(userId);
		if (storedUser == null) {
			throw new UserNotExistException();
		}
		
		group.setUser(storedUser);
		
		if (fileName.length() != 0) {
			group.setImgAddr(fileName);
		}
		
		Group storedGroup = groupRepository.save(group);

		if (fileName.length() != 0 ) { // 디비에 모임이 저장된 후에 이미지를 저장한다.
			Utils.saveImg(mFile, storedGroup.getGroupId().toString(), fileName);
		}
		
		return storedGroup;
	}
	
	/**
	 * 모임 정보를 수정한다.
	 * @param mFile 멀티파트 파일
	 * @param group 모임 정보
	 * @param userId 수정자
	 * @param fileName 저장될 파일 이름
	 * @return 성공할 경우 저장된 모임 객체를 반환
	 * @throws MismatchPatternException validation 통과 못할 때 발생
	 * @throws NotExistException 존재하지 않는 모임이나 유저일때 발생
	 * @throws IllegalStateException 파일명이 올바르지 않을 때 발생
	 * @throws IOException 파일 쓰기에 실패 햇을 때 발생
	 */
	@Transactional
	public Group update(MultipartFile mFile, Group group, Long userId, String fileName) throws MismatchPatternException, 
																							   NotExistException,
																							   IllegalStateException,
																							   IOException {
		boolean titleCheck = Pattern.matches(".{5,100}", group.getTitle());
		boolean recruitingPolicyCheck = Pattern.matches("^FCFS$|^SELECTION$", group.getRecruitingPolicy());
		boolean pDateCheck = group.getPsDate().isBefore(group.getPfDate());
		boolean rDateCheck = group.getRsDate().isBefore(group.getRfDate());
		boolean prDateCheck = (group.getRfDate().isEqual(group.getPsDate()) 
								|| group.getRfDate().isBefore(group.getPsDate()));
		boolean personnelCheck = group.getPersonnel() != 0;
		boolean categoryCheck = Pattern.matches("^교육$|^강연$|^취미활동$|^여행$", group.getCategory());
		boolean addressCheck = Pattern.matches(".{10,100}", group.getAddress());
		boolean simpleInfoCheck = Pattern.matches(".{10,100}", group.getSimpleInfo());
		boolean detailInfoCheck = Pattern.matches(".{0,2000}", group.getDetailInfo());
		
		if ( !(titleCheck && recruitingPolicyCheck && pDateCheck && rDateCheck && prDateCheck && personnelCheck
				&& categoryCheck && addressCheck && addressCheck && simpleInfoCheck && detailInfoCheck)) {
			throw new MismatchPatternException();
		}		
		
		try {
			String address = group.getAddress();
			String[] addressArray = address.split(" ");
			String addressDo = addressArray[0];
			group.setAddressDo(addressDo);
			
			User storedUser =userRepository.findByUserId(userId);
			Group storedGroup = groupRepository.findByGroupIdAndUser(group.getGroupId(), storedUser);
			
			storedGroup.update(group);
			
			if (fileName.length() != 0) {
				storedGroup.setImgAddr(fileName);
			}
			
			groupRepository.save(storedGroup);
			
			if (fileName.length() != 0 ) { // 이미지 변경이 있는 경우 변경하고 없는 경우 그대로 유지
				Utils.saveImg(mFile, storedGroup.getGroupId().toString(), fileName);
			}
			
			return storedGroup;
		} catch (NullPointerException e) {
			throw new NotExistException();
		}
	}

	/**
	 * 모임을 삭제한다.
	 * @param groupId 모임 아이디
	 * @param userId 요청자 아이디
	 * @throws NotExistException 존재하지 않는 모임이나 유저일때 발생한다.
	 */
	@Transactional
	public void deleteByGroupId(Long groupId, Long userId) throws NotExistException {
		try {
			User storedUser = userRepository.findByUserId(userId);
			Group targetGroup = groupRepository.findByGroupIdAndUser(groupId, storedUser); 
			groupRepository.delete(targetGroup);
			
			String path = "C:/img";
			String OS = BoardApplication.OS;
			if (OS.indexOf("win") >= 0) {
				path = path + File.separator + "groups";
			} else if (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") >= 0) {
				path = "/home/img/groups";
			}
			path = path + File.separator + groupId;
			File dir = new File(path);
			if (dir.isDirectory()) {// 디렉토리가 존재하면 있던 이미지 모두 삭제
				File[] files = dir.listFiles();
				for (File file : files) {
					file.delete();
				}
				dir.delete();
			}
		} catch (InvalidDataAccessApiUsageException | IllegalArgumentException e) {
			throw new NotExistException();
		}
	}
	
	
}
