package io.hhplus.tdd.point;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.constant.TransactionType;
import io.hhplus.tdd.point.dto.request.ChargeRequest;
import io.hhplus.tdd.point.dto.request.UseRequest;
import io.hhplus.tdd.point.dto.response.PointHistory;
import io.hhplus.tdd.point.dto.response.UserPoint;
import io.hhplus.tdd.point.exception.InsufficientPointException;

@Service
@Slf4j
@RequiredArgsConstructor
public class PointService {

    private final PointHistoryTable pointHistoryTable;
    private final UserPointTable    userPointTable;

    public UserPoint findUserPointById(long userId) {
        return userPointTable.selectById(userId);
    }

    public List<PointHistory> findAllHistory(long userId) {
        return pointHistoryTable.selectAllByUserId(userId);
    }

    public UserPoint chargeByUserId(long userId, ChargeRequest request) {
        pointHistoryTable.insert(userId, request.amount(), TransactionType.CHARGE, System.currentTimeMillis());
        return userPointTable.insertOrUpdate(userId, request.amount());
    }

    public UserPoint usePointByUserId(long userId, UseRequest request) {
        UserPoint userPoint = userPointTable.selectById(userId);
        if (userPoint.point() < request.amount()) {
            log.warn("포인트 부족: userId={}, point={}", userId, request.amount());
            throw new InsufficientPointException();
        }

        pointHistoryTable.insert(userId, request.amount(), TransactionType.USE, System.currentTimeMillis());

        return userPointTable.insertOrUpdate(userId, userPoint.point() - request.amount());
    }

}
