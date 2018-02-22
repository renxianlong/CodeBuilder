 <select id="searchCustomer" parameterType="cn.idongjia.deed.lib.dto.customer.router.BosCustomerSearchDTO" resultMap="customerSearchResultMap">
        SELECT
        dj_c.id,kp_r.ooid,kp_r.order_item_id,kp_r.uid,kp_r.suid,kp_r.rid
        FROM dj_customer dj_c
        LEFT JOIN kp_refund kp_r ON kp_r.rid = dj_c.refund_id
        <choose>
            <when test="orderId != null and orderItemId != null">
                LEFT JOIN dj_order dj_o ON kp_r.ooid = dj_o.id
                LEFT JOIN dj_order_item dj_oi ON kp_r.order_item_id = dj_oi.id
            </when>
            <when test="orderId != null">
                LEFT JOIN dj_order dj_o ON kp_r.ooid = dj_o.id
            </when>
            <when test="orderItemId != null">
                LEFT JOIN dj_order_item dj_oi ON kp_r.order_item_id = dj_oi.id
            </when>
        </choose>
        <choose>
            <when test="buyerName != null and sellerName != null">
                LEFT JOIN kp_user kp_u ON kp_u.uid = kp_r.uid
                LEFT JOIN kp_user kp_su ON kp_su.uid = kp_r.suid
            </when>
            <when test="buyerName != null">
                LEFT JOIN kp_user kp_u ON kp_u.uid = kp_r.uid
            </when>
            <when test="sellerName != null">
                LEFT JOIN kp_user kp_su ON kp_su.uid = kp_r.suid
            </when>
        </choose>
        <where>
            <if test="orderId != null">AND dj_o.id = #{orderId}</if>
            <if test="orderItemId != null">AND kp_r.order_item_id = #{orderItemId}</if>
            <if test="customerStatus != null">AND dj_c.status = #{customerStatus}</if>
            <if test="buyerName != null">AND kp_u.`username` LIKE #{buyerName}</if>
            <if test="sellerName != null">AND kp_su.`username` LIKE #{sellerName}</if>
            <if test="start != null and start != 0">AND dj_c.create_time <![CDATA[ > ]]> #{start}</if>
            <if test="end != null and end != 0">AND dj_c.create_time <![CDATA[ < ]]> ${end}</if>
            <if test="true">AND kp_r.order_item_id != 0</if>
        </where>
        <if test="orderBy != null">ORDER BY kp_r.${orderBy}</if>
        <if test="limit != null">LIMIT ${limit}</if>
        <if test="offset != null">OFFSET ${offset}</if>
    </select>