package com.bytecamp.web.cheat;

import com.bytecamp.web.dto.RequestDTO;

/**
 * @author wangke
 * @description: CheatingCheck
 * @date 2019-08-27 23:52
 */
public interface CheatingCheck {

    /**
     * 返回真证明作弊
     * @param dto
     * @return
     */
    Boolean check(RequestDTO dto);
}
