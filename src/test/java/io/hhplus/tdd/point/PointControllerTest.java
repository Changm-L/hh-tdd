package io.hhplus.tdd.point;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hhplus.tdd.point.constant.TransactionType;
import io.hhplus.tdd.point.dto.request.ChargeRequest;
import io.hhplus.tdd.point.dto.request.UseRequest;
import io.hhplus.tdd.point.dto.response.PointHistory;
import io.hhplus.tdd.point.dto.response.UserPoint;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PointController.class)
class PointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PointService pointService;

    @Test
    void point() throws Exception {
        //given
        long userId = 1L;
        long point = 1000L;
        UserPoint userPoint = new UserPoint(userId, point, System.currentTimeMillis());
        when(pointService.findUserPointById(userId)).thenReturn(userPoint);

        //when & then
        mockMvc.perform(get("/point/{id}", userId))
               .andExpect(status().isOk())
               .andExpect(jsonPath("id").value(userPoint.id()))
               .andExpect(jsonPath("point").value(userPoint.point()))
               .andExpect(jsonPath("updateMillis").value(userPoint.updateMillis()));

    }

    @Test
    void history() throws Exception {
        //given
        long userId = 1L;
        PointHistory pointHistory1 = new PointHistory(
                1L,
                userId,
                100L,
                TransactionType.CHARGE,
                System.currentTimeMillis()
        );
        PointHistory pointHistory2 = new PointHistory(
                2L,
                userId,
                100L,
                TransactionType.USE,
                System.currentTimeMillis()
        );
        List<PointHistory> histories = List.of(pointHistory1, pointHistory2);
        when(pointService.findAllHistory(userId)).thenReturn(histories);

        //when & then
        mockMvc.perform(get("/point/{id}/histories", userId))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(histories.size()))
               .andExpect(jsonPath("$[0].id").value(pointHistory1.id()))
               .andExpect(jsonPath("$[0].userId").value(pointHistory1.userId()))
               .andExpect(jsonPath("$[0].amount").value(pointHistory1.amount()))
               .andExpect(jsonPath("$[0].type").value(pointHistory1.type().name()))
               .andExpect(jsonPath("$[0].updateMillis").value(pointHistory1.updateMillis()))
               .andExpect(jsonPath("$[1].id").value(pointHistory2.id()))
               .andExpect(jsonPath("$[1].userId").value(pointHistory2.userId()))
               .andExpect(jsonPath("$[1].amount").value(pointHistory2.amount()))
               .andExpect(jsonPath("$[1].type").value(pointHistory2.type().name()))
               .andExpect(jsonPath("$[1].updateMillis").value(pointHistory2.updateMillis()));
    }

    @Test
    void charge() throws Exception {
        //given
        long userId = 1L;
        long point = 1000L;
        ChargeRequest request = new ChargeRequest(point);
        UserPoint userPoint = new UserPoint(userId, point, System.currentTimeMillis());
        when(pointService.chargeByUserId(userId, request)).thenReturn(userPoint);

        //when & then
        mockMvc.perform(
                       patch("/point/{id}/charge", userId)
                               .contentType(MediaType.APPLICATION_JSON)
                               .content(objectMapper.writeValueAsString(request))
               )
               .andExpect(status().isOk())
               .andExpect(jsonPath("id").value(userPoint.id()))
               .andExpect(jsonPath("point").value(userPoint.point()))
               .andExpect(jsonPath("updateMillis").value(userPoint.updateMillis()));

    }

    @Test
    void use() throws Exception {
        //given
        long userId = 1L;
        long point = 1000L;
        UseRequest request = new UseRequest(point);
        UserPoint userPoint = new UserPoint(userId, point, System.currentTimeMillis());
        when(pointService.usePointByUserId(userId, request)).thenReturn(userPoint);

        //when & then
        mockMvc.perform(
                       patch("/point/{id}/use", userId)
                               .contentType(MediaType.APPLICATION_JSON)
                               .content(objectMapper.writeValueAsString(request))
               )
               .andExpect(status().isOk())
               .andExpect(jsonPath("id").value(userPoint.id()))
               .andExpect(jsonPath("point").value(userPoint.point()))
               .andExpect(jsonPath("updateMillis").value(userPoint.updateMillis()));
    }
}