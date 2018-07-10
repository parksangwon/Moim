package com.navercorp.board.constant;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import com.navercorp.board.BoardApplication;
import com.navercorp.board.domain.Group;

public class Utils {

	/**
	 * 로그인 하지 않았을때 그룹을 조회하면 개설자의 정보를 *표로 바꿔서 표시해준다.
	 * 
	 * @param target 바꿀 정보
	 * @return *표로 바뀐 정보
	 */
	public static String changeToAsterisk(String target) {
		return target.replaceAll("[a-zA-Z0-9가-힣]", "*");
	}
	
	/**
	 * 모임정보와 요청자 아이디를 바탕으로 모임의 컨디션을 반환한다.
	 * @param group 모임정보
	 * @param userId 지원자 아이디
	 * @return 요청자와 모임의 컨디션을 반환
	 */
	public static String checkGroupCondition(Group group, Long userId) {
		String condition = Const.NONE;
		LocalDateTime now = LocalDateTime.now();

		if (group.getPfDate().isBefore(now)) { // 모임 기간 확인
			condition = Const.PROJECT_FINISHED;
		} else if (group.getRfDate().isBefore(now)) { // 모집 종료 시간 확인
			condition = Const.RECRUIT_FINISHED;
		} else if (group.getRsDate().isAfter(now)) { // 모집 시작 시간 확인
			condition = Const.BEFORE_RECRUIT;
		} else if (group.hasApplyUser(userId)) { // 신청 확인
			condition = Const.APPLIED;
		} else if (group.getRecruitingPolicy().equals(Const.METHOD_SELECTION)) { // 선정 방식
		    	condition = Const.METHOD_SELECTION;
		} else if (group.getPersonnel() <= group.getApplyList().size()) { // 인원 확인
			condition = Const.FULL_OF_PERSONNEL;
		}
		return condition;
	}
	
	/**
	 * 유저의 모임 추천 상태를 확인한다.
	 * @param group
	 * @param userId
	 * @return 신청
	 */
	public static String checkRecommendCondition(Group group, Long userId) {
		String condition = Const.NONE;
		if (group.hasRecommendUser(userId)) {
			condition = Const.RECOMMEND;
		}
		return condition;
	}
	
	/**
	 * 이미지 파일을 저장한다.
	 * @param mFile 멀티파트 파일
	 * @param folderName 폴더 이름(모임 번호)
	 * @param originFileName 파일 이름(저장할 파일 이름)
	 * @throws IllegalStateException 파일 명에 문제가 있을 경우
	 * @throws IOException 저장되지 않은 경우
	 */
	public static void saveImg(MultipartFile mFile, String folderName, String originFileName) throws IllegalStateException,
																									 IOException {
		// 파일 저장
		String path = "C:/img";
		String OS = BoardApplication.OS;
		if (OS.indexOf("win") >= 0) {
			path = path + File.separator + "groups";
		} else if (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") >= 0) {
			path = "/home/img/groups";
		}
		
		if (originFileName.length() != 0) { // 1-1. 파일 업로드 했을 때
			path = path + File.separator + folderName;
			File dir = new File(path);
			if (!dir.isDirectory()) {
				dir.mkdirs();
			} else { // 디렉토리가 존재하면 있던 이미지 모두 삭제
				File[] files = dir.listFiles();
				for (File file : files) {
					file.delete();
				}
			}
			File serverFile = new File(path + File.separator + originFileName);
			mFile.transferTo(serverFile);
		}
	}
}
