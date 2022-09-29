package com.fkp.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: fkp
 * @time: 2022-09-29 23:37:38
 * @description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationLog implements Serializable {
    private String operationCode;

    private String operationDesc;

    private String uri;

    private String token;

    private String result;

    private static final long serialVersionUID = 1L;
}
