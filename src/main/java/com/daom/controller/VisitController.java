package com.daom.controller;

import com.daom.config.auth.UserDetailsImpl;
import com.daom.domain.Member;
import com.daom.dto.response.RestResponse;
import com.daom.service.ResponseService;
import com.daom.service.VisitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/visit")
@RestController
public class VisitController {
    private final VisitService visitService;
    private final ResponseService responseService;


    @PostMapping("/{shopId}")
    public RestResponse visit(@AuthenticationPrincipal UserDetailsImpl userDetails,
                              @PathVariable("shopId") Long shopId) {
        Member loginMember = userDetails.getMember();
        visitService.visit(loginMember, shopId);
        return responseService.getSuccessResponse();
    }

    @GetMapping("/check/{shopId}")
    public RestResponse isVisitInPeriod(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                        @PathVariable("shopId") Long shopId,
                                        @RequestParam(value = "period", defaultValue = "3") int period
    ) {
        Member loginMember = userDetails.getMember();
        boolean visit = visitService.isVisitInPeriod(loginMember, shopId, period);

        return responseService.getSingleResponse(visit);
    }
}
