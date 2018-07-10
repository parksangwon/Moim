package com.navercorp.board.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.navercorp.board.domain.Group;
import com.navercorp.board.domain.User;

public interface GroupRepository extends JpaRepository<Group, Long> {

	/**
	 * groupId로 그룹 조회
	 * @param groupId
	 * @return 조회된 그룹
	 */
	Group findByGroupId(Long groupId);

	/**
	 * groupId와 user로 그룹 조회
	 * @param groupId
	 * @param user 작성자
	 * @return 조회된 그룹
	 */
	Group findByGroupIdAndUser(Long groupId, User user);

	/**
	 * 메인 페이지에서 나오는 모임 조회
	 * @return 모집종료 되지 않은 그룹들만 반환
	 */
	@Query("SELECT DISTINCT g FROM Group g left join fetch g.applyList WHERE g.rfDate > :now ORDER BY g.createTime DESC")
	List<Group> findAllAppliableGroups(@Param("now") LocalDateTime now);

	/**
	 * 입력받은 키워드로 모임의 title, simpleInfo를 조회해 group list를 반환한다.
	 * @param keyword 검색어
	 * @return title과 simpleInfo에 keyword가 포함된 group list
	 */
	@Query(value = 
			  "SELECT * "
			+ "FROM groups g "
			+ "WHERE "
				+ "g.title LIKE CONCAT('%', :keyword, '%') "
				+ "OR g.simple_info LIKE CONCAT('%', :keyword, '%') "
			+ "ORDER BY "
				+ "g.create_time DESC",
			nativeQuery = true)
	List<Group> findGroupsBySearchKeyword(@Param("keyword") String keyword);
	
	/**
	 * 필터링된 모임 리스트를 조회한다.
	 * @param category 카테고리
	 * @param addressDo 도단위 지역
	 * @param time 오전/오후
	 * @param weekdateStart 시작 요일, (월=0 ~ 일=6)
	 * @param weekdateEnd 종료 요일, (월=0 ~ 일=6)
	 * @return 조건에 알맞는 모임 목록
	 */
	@Query(value = 
			  "SELECT * "
			+ "FROM groups g "
			+ "WHERE "
				+ "( :category LIKE CONCAT('%', g.category, '%') ) "
				+ "AND ( :addressDo LIKE CONCAT('%', g.address_do, '%') ) "
				+ "AND ( "
						+ "( :weekdateStart <= WEEKDAY(g.project_start_datetime) AND WEEKDAY(g.project_start_datetime) <= :weekdateEnd )"
						+ "AND ( :weekdateStart <= WEEKDAY(g.project_finish_datetime) AND WEEKDAY(g.project_finish_datetime) <= :weekdateEnd )"
				+ ")"
				+ "AND ( :time LIKE CONCAT('%', DATE_FORMAT(g.project_start_datetime, '%p'), '%') ) "
				+ "AND ( now() < g.recruiting_finish_datetime ) "
			+ "ORDER BY "
				+ "g.create_time DESC",
				nativeQuery = true)
	List<Group> findGroupsByFiltering(@Param("category") String category, @Param("addressDo") String addressDo,
											@Param("time") String time, @Param("weekdateStart") int weekdateStart,
											@Param("weekdateEnd") int weekdateEnd);
	
	/**
	 * 필터링된 모임 리스트를 조회한다.(키워드 포함)
	 * @param keyword 검색어
	 * @param category 카테고리
	 * @param addressDo 도단위 지역
	 * @param time 오전/오후
	 * @param weekdateStart 시작 요일, (월=0 ~ 일=6)
	 * @param weekdateEnd 종료 요일, (월=0 ~ 일=6)
	 * @return 조건에 알맞는 모임 목록
	 */
	@Query(value = 
			  "SELECT * "
			+ "FROM groups g "
			+ "WHERE "
				+ "( g.title LIKE CONCAT('%', :keyword, '%') OR g.simple_info LIKE CONCAT('%', :keyword, '%') ) "
				+ "AND ( :category LIKE CONCAT('%', g.category, '%') ) "
				+ "AND ( :addressDo LIKE CONCAT('%', g.address_do, '%') ) "
				+ "AND ( "
						+ "( :weekdateStart <= WEEKDAY(g.project_start_datetime) AND WEEKDAY(g.project_start_datetime) <= :weekdateEnd )"
						+ "AND ( :weekdateStart <= WEEKDAY(g.project_finish_datetime) AND WEEKDAY(g.project_finish_datetime) <= :weekdateEnd )"
				+ ")"
				+ "AND ( :time LIKE CONCAT('%', DATE_FORMAT(g.project_start_datetime, '%p'), '%') ) "
				+ "AND ( now() < g.recruiting_finish_datetime ) "
			+ "ORDER BY "
					+ "g.create_time DESC",
				nativeQuery = true)
	List<Group> findGroupsByFilteringWithKeyword(@Param("keyword") String keyword, @Param("category") String category,
											  @Param("addressDo") String addressDo, @Param("time") String time,
											  @Param("weekdateStart") int weekdateStart, @Param("weekdateEnd") int weekdateEnd);

}
