var ticketList = [];
var ticket = {title : "title", type : "type", price : 1};


function getManifestations() {
	localStorage.removeItem("resultManifestation");
	localStorage.removeItem("dataForSort");
	$.ajax({
		url: "/manifestations?active=true",
		method: "get",
		dataType: "JSON",
		success: function (data) {
			console.log(data);
			var i = 0;
			for (i; i < data.length; i++) {
				let r = 0;
				let f = 0;
				let v = 0;

				let rr = data[i].availableRegularTickets;
				let ff = data[i].availableFanpitTickets;
				let vv = data[i].availableVipTickets;
				
				let regular = (rr != 0) ? "visible" : "disabled";
				let fanpit = (data[i].availableFanpitTickets != 0) ?  "visible" : "disabled";
				let vip = (data[i].availableVipTickets != 0) ? "visible" : "disabled";
				let price = (data[i].price);
				let regularNumber = "";

				var rating = data[i].averageRating === 0 ? "/" : data[i].averageRating;
				var date = new Date(data[i].realisationDate);
				if(localStorage.getItem("role") === 'BUYER'){

					
				$("#cards").append(
					$("<div class='col-md-4 div-space'>").append(
						$("<div class='card'>")
							.append(
								$("<div class='card-header' >").append(
									$("<h5>").text(data[i].manifestationType)
								)
							)
							.append($("<img class='image' src='" + data[i].posterPath + "'  id='"+data[i].title +"' title='Show manifestation'>").click(function () { showManifestation(this.id); }))
							.append(
								$("<div class='card-body text-center'>")
									.append(
										$("<div class='card-title'>").append(
											$("<h5>").text(data[i].title)
										)
									)
									.append(
										$("<p class='card-text'>").text(
											date.toLocaleString("sr-sp")
										)
									)
									.append(
										$("<p class='card-text'>").text(
											data[i].location.street +
											" " +
											data[i].location.number +
											", " +
											data[i].location.city
										)
									)
							)
							.append(
								$('<div class="card-footer" >')
									.append($("<h5>").text("Book a ticket:"))
									.append($("<p class='card-text'>")
										.append($('<button id ="' + data[i].title+1+ '" class = "btn" ' + regular + ' >').attr("disabled", !data[i].isActive || rr === 0).
										text("Regular " + data[i].price + "RSD ").click(function () { bookTicket(this.id, "regular", price,i); rr--; console.log(rr); if(rr === 0){$("#"+this.id+"").css("visibility", "hidden");alert("There is no more of these tickets!"); }else{r++;} })))
				
						.append(
							$("<p class='card-text'>")						
							.append($("<p class='card-text'>")
							.append($('<button id ="' + data[i].title+2 + '" class = "btn" ' + fanpit + '>').attr("disabled", !data[i].isActive || ff === 0).
							text("Fanpit " + data[i].price * 2 + " RSD").click(function () { bookTicket(this.id, "fanpit", price,i); ff--; if(ff === 0) { $("#"+this.id+"").css("visibility", "hidden"); alert("There is no more of these tickets!");} })))
						)
						.append(
							$("<p class='card-text'>")
							.append($('<button id ="' + data[i].title+3 + '" class = "btn"' + vip + ' >').attr("disabled", !data[i].isActive || vv === 0).
							text("Vip " + data[i].price * 4 + " RSD").click(function () { bookTicket(this.id, "vip", price,i);vv--; if(vv === 0) {$("#"+this.id+"").css("visibility", "hidden"); alert("There is no more of these tickets!"); }  })))
				)
					)
				);	}else if(localStorage.getItem("role") === 'ADMIN'){
					$("#cards")
					.append($("<div class='col-md-4 div-space'>")
							.append($("<div class='card' >")
									.append($("<div class='card-header d-flex justify-content-between align-items-center'>")
											.append($("<h5>").text(data[i].manifestationType))
											.append($("<div class='p-2'>")
											.append($('<button id ="' + data[i].title + '" class = "btn"><i class="fa fa-check" aria-hidden="true"></i></button>').attr("hidden", data[i].isActive).click(function () { approveManifestation(this.id); }))
											.append($('<button id ="' + data[i].title + '" class = "btn"><i class="fa fa-trash" aria-hidden="true"></i></button>').click(function () { deleteManifestation(this.id); }))))
									.append($("<img  class='image'  src='"+data[i].posterPath+"'  id='"+data[i].title +"' title='Show manifestation'>").click(function () { showManifestation(this.id); }))
									
									.append($("<div class='card-body text-center'>")
											.append($("<div class='card-title'>").append($("<h5>").text(data[i].title)))
											.append($("<p class='card-text'>").text(data[i].realisationDate))
											.append($("<p class='card-text'>").text(data[i].location.street +" " + data[i].location.number + ", " + data[i].location.city))
										
											)
									.append($("<div class='card-footer'>").text("Regular price: " + data[i].price +" RSD").append($("<p class='card-text p_text'>").text("Rating: " + rating)))
											));
				}else{
					$("#cards")
					.append($("<div class='col-md-4 div-space'>")
							.append($("<div class='card'>")
									.append($("<div class='card-header'>").append($("<h5>").text(data[i].manifestationType)))
									.append($("<img  class='image'  src='"+data[i].posterPath+"'   id='"+data[i].title +"' title='Show manifestation'>").click(function () { showManifestation(this.id); }))
									.append($("<div class='card-body text-center'>")
											.append($("<div class='card-title'>").append($("<h5>").text(data[i].title)))
											.append($("<p class='card-text'>").text(data[i].realisationDate))
											.append($("<p class='card-text'>").text(data[i].location.street +" " + data[i].location.number + ", " + data[i].location.city))
										
											)
									.append($("<div class='card-footer'>").text("Regular price: " + data[i].price +" RSD").append($("<p class='card-text p_text'>").text("Rating: " + rating)))
											));
				}
			}
		},
	});
}

$(document).ready(function(){
    $("#toPrice").on("input", function(){
    	var toPrice = $("#toPrice").val();
    	if(toPrice==""){
			$("#fromPrice").css('background-color', "#FFFFFF");
			$("#search").prop("disabled",false);
		}
    	else if(toPrice < 500 || toPrice > 3000){
			$("#toPrice").css('background-color',"rgba(255,0,0,0.5)");
			$("#search").prop("disabled",true);
		}else{
			$("#toPrice").css('background-color', "#FFFFFF");
			$("#search").prop("disabled",false);
		}
    });
    
    $("#fromPrice").on("input", function(){
    	var fromPrice = $("#fromPrice").val();
    	if(fromPrice==""){
			$("#fromPrice").css('background-color', "#FFFFFF");
			$("#search").prop("disabled",false);
		}
    	else if(fromPrice < 500 || fromPrice > 3000){
			$("#fromPrice").css('background-color', "rgba(255,0,0,0.5)");
			$("#search").prop("disabled",true);
		}else{
			$("#fromPrice").css('background-color', "#FFFFFF");
			$("#search").prop("disabled",false);
			}
    });

    
});

function search(){
	console.log("Uso sam");
	var title = $("#title").val();
	var location = $("#location").val();
	var fromDate = $("#datepicker").val();
	var toDate = $("#datepicker2").val();
	var fromPrice = $("#fromPrice").val()  == "" ? 500 :  parseInt($("#fromPrice").val());
	var toPrice = $("#toPrice").val() == "" ? 3000 :  parseInt($("#toPrice").val());
	console.log(title);
	console.log(location);
	console.log(fromDate);
	console.log(toDate);
	console.log(fromPrice);
	console.log(toPrice);
	dataForSearch = JSON.stringify({ "title": title, "location" : location, "fromDate" : fromDate, "toDate" : toDate, "fromPrice" : fromPrice, "toPrice" : toPrice });
	$.ajax({
        url:"/manifestations/search",
        method:"post",
        contentType: "application/json",
        data: dataForSearch,
        dataType: "JSON",
        success:function(data){
            console.log(data);
            localStorage.setItem('resultManifestation', JSON.stringify(data));
            console.log(localStorage.getItem('resultManifestation'));
            if(localStorage.getItem('role') !== null){
            	window.location = "searchResultManUsers.html";
            }else{
            	window.location = "searchResultManifestation.html";
            }
            
        },
        error: function(xhr, textStatus, error){
            console.log(xhr.statusText);
            console.log(textStatus);
            console.log(error);
        }
	});
		
}

function goToHomepage(){
	if(localStorage.getItem('role') === null){
		location.href = "homepage.html";
	}else if(localStorage.getItem('role') === 'ADMIN'){
		location.href = "homepageAdmin.html";
	}else if(localStorage.getItem('role') === 'SELLER'){
		location.href = "homepageSeller.html";
	}else if(localStorage.getItem('role') === 'BUYER'){
		location.href = "homepageBuyer.html";
	}
}

function bookTicket(title, typeOfTicket, regularPrice, i){
	var price;
	var totalPrice = 0;
	if(typeOfTicket === "regular"){
		price = regularPrice;
	}else if( typeOfTicket === "fanpit"){
		price = regularPrice * 2;
	}else{
		price = regularPrice * 4;
	}

	title = title.slice(0, -1);
	var newTicket = {title : title, typeOfTicket : typeOfTicket, price : price};
	ticketList.push(newTicket);
	discount = (100 - localStorage.getItem("discount"))/100;

	ticketList.forEach(ticket => {
		totalPrice = (totalPrice + ticket.price) ;
	});

	console.log(totalPrice);
	console.log(ticketList);
	dataForBooking = JSON.stringify({ "title": title, "typeOfTicket" : typeOfTicket});

	$("#tickets").append($("<div class='card-footer'>").text("Ticket: " + title + " " + typeOfTicket + " " + price +" RSD"));
	$("#price").text("Total price: " + totalPrice +" RSD   " + "With discount: " +  Math.round(totalPrice* discount)+ " RSD");
	// $("#price").css("visibility", "visible");
	$("#button1").css("visibility", "visible");
}

function confirmBooking(){
	console.log(ticketList);
	$.ajax({
        url:"/manifestations/bookTicket",
        method:"post",
        contentType: "application/json",
        data: JSON.stringify(ticketList),
        dataType: "JSON",
        success:function(data){
            console.log(data);
			alert("You have successfully booked a ticket.")
			location.href = "homepageBuyer.html";
        },
        error: function(xhr, textStatus, error){
            console.log(xhr.statusText);
            console.log(textStatus);
            console.log(error);
        }
	});
}

function deleteManifestation(title){
	console.log(title);
	$.ajax({
        url:"/manifestations/" + title,
        method:"delete",
       
        success:function(data){
            console.log(data);
			alert("You have successfully deleted a manifestation.")
			location.href = "homepageAdmin.html";
        },
        error: function(xhr, textStatus, error){
            console.log(xhr.statusText);
            console.log(textStatus);
            console.log(error);
        }
	});
}

function getInactiveManifestations(){
	 if(localStorage.getItem("role") !== 'ADMIN'){
		 goToHomepage();
	 }
	localStorage.removeItem("resultManifestation");
	localStorage.removeItem("dataForSort");
	$.ajax({
		url: "/manifestations?active=false",
		method: "get",
		dataType: "JSON",
		success: function (data) {
			console.log(data);
			var i = 0;
			for (i; i < data.length; i++) {
			
		
					$("#cards")
					.append($("<div class='col-md-4 div-space'>")
							.append($("<div class='card'>")
									.append($("<div class='card-header d-flex justify-content-between align-items-center'>").append($("<h5>").text(data[i].manifestationType)).append($('<button id ="' + data[i].title + '" class = "btn"><i class="fa fa-check" aria-hidden="true"></i></button>').click(function () { approveManifestation(this.id); })))
									.append($("<img src='"+data[i].posterPath+"'>"))
									
									.append($("<div class='card-body text-center'>")
											.append($("<div class='card-title'>").append($("<h5>").text(data[i].title)))
											.append($("<p class='card-text'>").text(data[i].realisationDate))
											.append($("<p class='card-text'>").text(data[i].location.street +" " + data[i].location.number + ", " + data[i].location.city))
										
											)
									.append($("<div class='card-footer'>").text("Regular price: " + data[i].price +" RSD"))
											));
				
			}
		},
	});
}

function approveManifestation(title){
	console.log(title);
	$.ajax({
		url: "/manifestations/approve/"+title,
		method: "put",
		dataType: "JSON",
		success: function (data) {
			console.log(data);
			window.location.reload();
		},
	});
}

function showManifestation(title){
	console.log(title);
	localStorage.setItem('showTitle', title);
	location.href = "showManifestation.html";
	
}

function getManifestation() {
	var title = localStorage.getItem('showTitle');
	console.log(title);
	$.ajax({
		url: "/manifestations/" + title,
		method: "get",
		dataType: "JSON",
		success: function (data) {
			
			console.log(data);
			var rating = data.averageRating === 0 ? "/" : data.averageRating;
			available =  parseInt(data.availableRegularTickets) + parseInt(data.availableFanpitTickets) + parseInt(data.availableVipTickets);
			$("#cards")
			.append($("<div class='col-md-4 div-space'>")
					.append($("<div class='card'>")
							.append($("<div class='card-header'>").append($("<h5>").text(data.manifestationType)))
							.append($("<img  class='image'  src='"+data.posterPath+"'   id='"+data.title +"'>"))
							.append($("<div class='card-body text-center'>")
									.append($("<div class='card-title'>").append($("<h5>").text(data.title)))
									.append($("<p class='card-text'>").text("Status: " + data.isActive))
									.append($("<p class='card-text'>").text("Date: " +data.realisationDate))
									.append($("<p class='card-text'>").text("Location: " + data.location.street +" " + data.location.number + ", " + data.location.city))
									.append($("<p class='card-text'>").text("Number of seats: " + data.availableTickets ))
									.append($("<p class='card-text'>").text("Remaining tickets: " +  available))
									)
							.append($("<div class='card-footer'>").text("Regular price: " + data.price +" RSD").append($("<p class='card-text p_text'>").text("Rating: " + rating)))
									));
		
			
		},
	});
}
