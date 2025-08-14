package io.hhplus.tdd.point;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import io.hhplus.tdd.point.dto.request.ChargeRequest;
import io.hhplus.tdd.point.dto.request.UseRequest;
import io.hhplus.tdd.point.dto.response.PointHistory;
import io.hhplus.tdd.point.dto.response.UserPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequiredArgsConstructor
@RequestMapping("/point")
public class PointController {

    private static final Logger log = LoggerFactory.getLogger(PointController.class);

    private final PointService pointService;

    /**
     * TODO - 특정 유저의 포인트를 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}")
    public UserPoint point(
            @Valid @PathVariable @Positive long id
    ) {
        return pointService.findUserPointById(id);
    }

    /**
     * TODO - 특정 유저의 포인트 충전/이용 내역을 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}/histories")
    public List<PointHistory> history(
            @Valid @PathVariable @Positive long id
    ) {
        return pointService.findAllHistory(id);
    }

    /**
     * TODO - 특정 유저의 포인트를 충전하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/charge")
    public UserPoint charge(
            @Valid @PathVariable @Positive long id,
            @Valid @RequestBody ChargeRequest request
    ) {
        return pointService.chargeByUserId(id, request);
    }

    /**
     * TODO - 특정 유저의 포인트를 사용하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/use")
    public UserPoint use(
            @Valid @PathVariable @Positive long id,
            @Valid @RequestBody UseRequest request
    ) {
        return pointService.usePointByUserId(id, request);
    }
}
