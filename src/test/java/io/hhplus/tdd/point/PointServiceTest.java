package io.hhplus.tdd.point;

import java.util.List;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.constant.TransactionType;
import io.hhplus.tdd.point.dto.request.ChargeRequest;
import io.hhplus.tdd.point.dto.request.UseRequest;
import io.hhplus.tdd.point.dto.response.PointHistory;
import io.hhplus.tdd.point.dto.response.UserPoint;
import io.hhplus.tdd.point.exception.InsufficientPointException;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class PointServiceTest {

    @Mock
    private PointHistoryTable pointHistoryTable;

    @Mock
    private UserPointTable userPointTable;

    @InjectMocks
    private PointService pointService;

    /**
     * 1. userId를 파라미터로 받는다
     * 2. userPointTable에서 userId로 포인트를 조회한다.
     * 3. userPointTable에서 반환 받은 userPoint를 반환한다.
     */
    @Nested
    class findUserPointById {
        @Test
        void 아이디가_없는_경우_empty를_반환한다() {
            /**
             * [작성 이유] : 비즈니스 로직 상 아이디가 없는 경우 새로 생성 후 반환
             * 하기 때문에 비즈니스 로직 검사를 위함
             */
            //given
            long userId = 1L;
            UserPoint expected = UserPoint.empty(userId);
            when(userPointTable.selectById(userId)).thenReturn(expected);

            //when
            UserPoint result = pointService.findUserPointById(userId);

            //then
            assertThat(expected).isEqualTo(result);
            verify(userPointTable).selectById(userId);
        }

        @Test
        void 아이디가_존재하면_테이블에_있는_값_반환() {
            /**
             * [작성 이유] : 데이터 조회 시 원하는 객체 조회를
             * 검증하기 위함
             */

            //given
            long userId = 1L;
            UserPoint expected = new UserPoint(userId, 100L, System.currentTimeMillis());
            when(userPointTable.selectById(userId)).thenReturn(expected);

            //when
            UserPoint result = pointService.findUserPointById(userId);

            //then
            assertThat(expected).isEqualTo(result);
            verify(userPointTable).selectById(userId);
        }
    }

    @Nested
    class findAllHistory {
        @Test
        void 포인트가_있는경우_리스트_형태로_반환한다() {
            /**
             * [작성 이유]: 조회를 원하는 userId에 대한
             * 조회 결과가 있는 경우 해당 리스트를 반환한다.
             */

            //given
            long userId = 1L;
            PointHistory pointHistory1 = mock(PointHistory.class);
            PointHistory pointHistory2 = mock(PointHistory.class);
            List<PointHistory> expected = List.of(pointHistory1, pointHistory2);

            when(pointHistoryTable.selectAllByUserId(userId)).thenReturn(expected);

            //when
            List<PointHistory> result = pointService.findAllHistory(userId);

            //then
            assertThat(expected).isEqualTo(result);
            verify(pointHistoryTable).selectAllByUserId(userId);
        }

        @Test
        void 포인트가_없는_경우_빈_리스트를_반환한다() {
            /**
             * [작성 이유]: 조회를 원하는 userId에 대한
             * 조회 결과가 없는 경우에는 빈 리스트를 반환 한다.
             */

            //given
            long userId = 1L;
            List<PointHistory> empty = List.of();
            when(pointHistoryTable.selectAllByUserId(userId)).thenReturn(empty);

            //when
            List<PointHistory> result = pointService.findAllHistory(userId);

            //then
            assertThat(result).isEmpty();
            verify(pointHistoryTable).selectAllByUserId(userId);
        }

    }

    @Nested
    class chargeByUserId {
        @Test
        void 유저가_존재하는_경우_포인트를_충전한다() {
            /**
             * [작성 이유]: 유저가
             */

            //given
            long userId = 1L;
            long amount = 100L;
            ChargeRequest request = new ChargeRequest(amount);
            UserPoint expected = new UserPoint(userId, request.amount(), System.currentTimeMillis());
            when(userPointTable.insertOrUpdate(userId, request.amount())).thenReturn(expected);

            //when
            UserPoint result = pointService.chargeByUserId(userId, request);

            //then
            assertEquals(expected, result);
            verify(pointHistoryTable).insert(
                    eq(userId),
                    eq(request.amount()),
                    eq(TransactionType.CHARGE),
                    anyLong()
            );
        }
    }

    @Nested
    class usePointByUserId {
        @Test
        void 포인트가_부족한_경우_InsufficientPointException이_발생한다() {
            //given
            long userId = 1L;
            UseRequest request = new UseRequest(100L);
            UserPoint expected = UserPoint.empty(userId);
            when(userPointTable.selectById(userId)).thenReturn(expected);

            //when & then
            assertThrows(
                    InsufficientPointException.class,
                    () -> pointService.usePointByUserId(userId, request)
            );
            verify(userPointTable).selectById(userId);
            verifyNoMoreInteractions(userPointTable);
        }

        @Test
        void 포인트가_충분한_경우_포인트를_사용한다() {
            //given
            long userId = 1L;
            long currentBalance = 200L;
            long useAmount = 100L;
            long expectedBalance = currentBalance - useAmount;
            UseRequest request = new UseRequest(useAmount);
            UserPoint current = new UserPoint(userId, currentBalance, System.currentTimeMillis());
            when(userPointTable.selectById(userId)).thenReturn(current);

            UserPoint expected = new UserPoint(userId, expectedBalance, System.currentTimeMillis());
            when(userPointTable.insertOrUpdate(userId, expectedBalance)).thenReturn(expected);

            //when
            UserPoint result = pointService.usePointByUserId(userId, request);

            //then
            assertThat(expected).isEqualTo(result);
            verify(userPointTable).selectById(userId);
            verify(pointHistoryTable).insert(
                    eq(userId),
                    eq(useAmount),
                    eq(TransactionType.USE),
                    anyLong()
            );
            verify(userPointTable).insertOrUpdate(userId, expectedBalance);
        }

    }

}