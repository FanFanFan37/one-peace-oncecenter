package com.treefintech.b2b.oncecenter.common.vo;

import com.treefinance.b2b.common.utils.file.CSVField;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author chengwei
 * @date 2018/10/17 下午4:29
 */
@Data
public class GongFuRepayInstMntDetailNotify implements Serializable {

    @CSVField(name = "apply_no")
    private String apply_no;

    @CSVField(name = "contract_no")
    private String contractNo;

    @CSVField(name = "seq_no")
    private String seqNo;

    /**
     * YYYY-MM-DD
     */
    @CSVField(name = "repay_date",patten = "yyyy-MM-dd")
    private Date payTradeTime;

    @CSVField(name = "curr_prin_bal")
    private BigDecimal balancePrincipal;

    @CSVField(name = "curr_int_bal")
    private BigDecimal balanceInterest;

    @CSVField(name = "curr_ovd_prin_pnlt_bal")
    private BigDecimal balanceOverdue;

    @CSVField(name = "curr_ovd_int_pnlt_bal")
    private BigDecimal balanceInterestOverdue;

    @CSVField(name = "repay_amt")
    private BigDecimal repayedAmount;

    @CSVField(name = "paid_prin_amt")
    private BigDecimal repayedPrincipal;

    @CSVField(name = "paid_int_amt")
    private BigDecimal repayedInterest;

    @CSVField(name = "paid_ovd_prin_pnlt_amt")
    private BigDecimal repayedOverdue;

    @CSVField(name = "paid_ovd_int_pnlt_amt")
    private BigDecimal repayedInterestOverdue;

    /**
     * 01、按期还款 02、提 前还款 03、逾期还款
     */
    @CSVField(name = "repay_amt_type")
    private String ioType;

    @CSVField(name = "term_no")
    private Integer periodIndex;

    /**
     * Y-结清/N-未结清
     */
    @CSVField(name = "clear_status")
    private String clearStatus;

    /**
     * Y-逾期/N-未逾期
     */
    @CSVField(name = "ovd_status")
    private String overdueStatus;

    /**
     * 商户ID
     */
    private Long merchantId;

    /**
     * 产品ID
     */
    private Long productId;
}
