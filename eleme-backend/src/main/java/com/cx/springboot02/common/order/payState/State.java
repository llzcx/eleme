package com.cx.springboot02.common.order.payState;

import com.cx.springboot02.common.order.Context;
import com.cx.springboot02.common.E.PayState;

public interface State {
   Boolean doAction(Context context);
   PayState getPayState();


}