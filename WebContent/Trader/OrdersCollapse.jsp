

 <%@page import="java.util.ArrayList"%>
<%@page import="java.util.TreeMap"%>
<%@page import="java.util.NavigableMap"%>
<div class="panel-group" id="accordion1">

  
  <%@page import="com.mt.bean.TraderManager"%>
<%@page import="com.mt.bean.Order"%>
    
  <%
  


  for (int q = 0; q < orders.size(); q ++){
	  if(!symbols.contains(orders.get(q).getSymbol())){
		  symbols.add(orders.get(q).getSymbol());
		  Tree.put(orders.get(q).getSymbol(), orders.get(q));
	  }
  } 
  
  
    %>
    
  <% for (int j = 0; j < symbols.size(); j ++){ %>

  <div class="panel panel-default">
    <div class="panel-heading">
      <h4 class="panel-title"><a class="panel-toggle" data-toggle="collapse" data-parent="accordion1" href="#collapseOuter<%=j%>">
       <%=symbols.get(j)%>
      </a></h4>
    </div>
    <div id="collapseOuter<%=j%>" class="panel-body collapse">
      <div class="panel-inner">

        <!-- Here we insert another nested accordion -->
        
        <% 
        boolean thereIsABuy = false; 
        for (Order order: orders){ 
        	if ((symbols.get(j).equals(order.getSymbol())) && (order.getSide().equals("buy"))){
        		thereIsABuy = true;
        		break;
        	}
        }
        if (thereIsABuy){
        %>
        <div class="panel-group" id="accordion2">
          <div class="panel panel-default">
            <div class="panel-heading">
              <h4 class="panel-title"><a class="panel-toggle" data-toggle="collapse" data-parent="accordion2" href="#collapseInnerOne<%=j%>">
              BUY
              </a></h4>
            </div>
            <div id="collapseInnerOne<%=j%>" class="panel-body collapse">
              <div class="panel-inner">
              <!-- <th>Type</th> -->
				<table class="table table-bordered table-hover">
					<tr>
						<th style="text-align: center">Order ID</th>
						<th style="text-align: center">Portfolio Manager</th>
						<th style="text-align: center">Creation Time</th>
						<th style="text-align: center">Status</th>
						<th style="text-align: center">Quantity</th>
						<th></th>
					</tr>

					<% for (Order order: orders){  
						
						int count = 0;  
						if ((symbols.get(j).equals(order.getSymbol())) && (order.getSide().equals("buy"))){%>
							
						<tr id=<%=count%>>
						<td><input type="checkbox" name="checkboxOptions" id="checkbox1"
							value="row1"><%= order.getId()%></td>
						<td style="text-align: center"><%=order.getPmName()%></td>
				
						<td style="text-align: center"><%=order.getTimeStamp()%></td>
						<td style="text-align: center"><%=order. getStatus()%>
						<td style="text-align: center"><%=order.getQuantity()%>						
							<button id=row1 type="button" class="btn btn-default"
								data-toggle="modal" data-target="#myModal1">Modify</button>
						</td>
						<td style="text-align: center"><button id=deleteorder1
								type="button" class="btn btn-default" data-toggle="popover"
								data-target="#Delete">Delete</button></td>				
				
					</tr>
												
					<% count++;}}%>
				
				</table>
												
				<%@include file="Modify-modal.jsp"%> 
              </div>
            </div>
          </div>
        <%} k=k+1;%>
        <% boolean thereIsASell = false; 
        for (Order order: orders){ 
        	if ((symbols.get(j).equals(order.getSymbol())) && (order.getSide().equals("sell"))){
        		thereIsASell = true;
        		break;
        	}
        }
        if (thereIsASell){
        %> 
          <div class="panel panel-default">
            <div class="panel-heading">
              <h4 class="panel-title"><a class="panel-toggle" data-toggle="collapse" data-parent="accordion2" href="#collapseInnerTwo<%=j%>">
               SELL
              </a></h4>
            </div>        
           <div id="collapseInnerTwo<%=j%>" class="panel-body collapse">
            <!-- <div id="collapseInnerTwo" class="panel-body collapse"> -->
              <div class="panel-inner">
                           <!-- <th>Type</th> -->
				<table class="table table-bordered table-hover">
					<tr>
						<th style="text-align: center">Order ID</th>
						<th style="text-align: center">Portfolio Manager</th>
						<th style="text-align: center">Creation Time</th>
						<th style="text-align: center">Status</th>
						<th style="text-align: center">Quantity</th>
						<th></th>
					</tr>

					<% for (Order order: orders){  
						
						int count = 0;  
						if ((symbols.get(j).equals(order.getSymbol())) && (order.getSide().equals("sell"))){%>
							
						<tr id=<%=count%>>
						<td><input type="checkbox" name="checkboxOptions" id="checkbox1"
							value="row1"><%= order.getId()%></td>
						<td style="text-align: center"><%=order.getPmName()%></td>
				
						<td style="text-align: center"><%=order.getTimeStamp()%></td>
						<td style="text-align: center"><%=order. getStatus()%>
						<td style="text-align: center"><%=order.getQuantity()%>
							<button id=row1 type="button" class="btn btn-default"
								data-toggle="modal" data-target="#myModal1">Modify</button>
						</td>
						
						<td style="text-align: center"><button id=deleteorder1
								type="button" class="btn btn-default" data-toggle="popover"
								data-target="#Delete">Delete</button></td>				
				
					</tr>
												
					<% count++;}}%>
				
				</table>
												
				<%@include file="Modify-modal.jsp"%>             
              </div>
            </div>
          </div>
        </div>
        <br>
<%} %>
        <!-- Inner accordion ends here -->



      </div>
    </div>
  </div>
   <% k=k+1;%>
 	<% }%>   


</div> 
