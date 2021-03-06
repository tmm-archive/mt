<div>

  <ul class="nav nav-tabs" role="tablist">
    <li role="presentation active" class="active"><a href="#open" aria-controls="open" role="tab" data-toggle="tab">Open</a></li>
    <li role="presentation"><a href="#sentForExecution" aria-controls="sentForExecution" role="tab" data-toggle="tab">Sent for Excecution</a></li>
    <li role="presentation"><a href="#successful" aria-controls="successful" role="tab" data-toggle="tab">Successful</a></li>
    <li role="presentation"><a href="#expired" aria-controls="expired" role="tab" data-toggle="tab">Expired</a></li>
    <li role="presentation"><a href="#partiallyFilled" aria-controls="partiallyFilled" role="tab" data-toggle="tab">Partially Filled</a></li>
    <li role="presentation"><a href="#cancelled" aria-controls="cancelled" role="tab" data-toggle="tab">Cancelled</a></li>
  </ul>

  <div class="tab-content">
  	<br>
    <div role="tabpanel" class="tab-pane fade in active" id="open">
    	<%@include file="BlocksCollapse.jsp" %>
    </div>
 	<div role="tabpanel" class="tab-pane  fade" id="sentForExecution">
    	<%@include file="BlocksCollapse.jsp" %>
    </div>
    <div role="tabpanel" class="tab-pane  fade" id="successful">
    <%@include file="BlocksCollapse.jsp" %>
    </div>
    <div role="tabpanel" class="tab-pane  fade" id="expired">
    	<%@include file="BlocksCollapse.jsp" %>
    </div>
    <div role="tabpanel" class="tab-pane  fade" id="partiallyFilled">
    	<%@include file="BlocksCollapse.jsp" %>
    </div>
    <div role="tabpanel" class="tab-pane  fade" id="cancelled">
    	<%@include file="BlocksCollapse.jsp" %>
    </div>
  </div>

</div>
