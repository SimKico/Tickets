function getManifestations() {
	localStorage.removeItem("resultManifestation");
	localStorage.removeItem("dataForSort");
	$.ajax({
		url: "/manifestations/all",
		method: "get",
		dataType: "JSON",
		success: function (data) {
			console.log(data);
			var i = 0;
			for (i; i < data.length; i++) {
				
				let regular = (data[i].availableRegularTickets != 0) ? "visible" : "disabled";
				let fanpit = (data[i].availableFanpitTickets != 0) ?  "visible" : "disabled";
				let vip = (data[i].availableVipTickets != 0) ? "visible" : "disabled";

				var date = new Date(data[i].realisationDate);
				if(localStorage.getItem("role") === 'BUYER'){

					
				$("#cards").append(
					$("<div class='col-md-4 div-space'>").append(
						$("<div class='card'>")
							.append(
								$("<div class='card-header'>").append(
									$("<h5>").text(data[i].manifestationType)
								)
							)
							.append($("<img src='" + data[i].posterPath + "'>"))
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
								$("<div class='card-footer'>")
									.append($("<h5>").text("Book a ticket:"))
									.append($("<p class='card-text'>")
										.append($('<button id ="' + data[i].title + '" class = "btn" ' + regular + ' >').
										text("Regular " + data[i].price  + " RSD").click(function () { bookTicket(this.id, "regular"); })))
				
						.append(
							$("<p class='card-text'>")						
							.append($("<p class='card-text'>")
							.append($('<button id ="' + data[i].title + '" class = "btn" ' + fanpit + '>').
							text("Fanpit " + data[i].price * 2 + " RSD").click(function () { bookTicket(this.id, "fanpit"); })))
						)
						.append(
							$("<p class='card-text'>")
							.append($('<button id ="' + data[i].title + '" class = "btn"' + vip + ' >').
							text("Vip " + data[i].price * 4 + " RSD").click(function () { bookTicket(this.id, "vip"); })))
				)
					)
				);	}else{
					$("#cards")
					.append($("<div class='col-md-4 div-space'>")
							.append($("<div class='card'>")
									.append($("<div class='card-header'>").append($("<h5>").text(data[i].manifestationType)))
									.append($("<img src='"+data[i].posterPath+"'>"))
									.append($("<div class='card-body text-center'>")
											.append($("<div class='card-title'>").append($("<h5>").text(data[i].title)))
											.append($("<p class='card-text'>").text(data[i].realisationDate))
											.append($("<p class='card-text'>").text(data[i].location.street +" " + data[i].location.number + ", " + data[i].location.city))
										
											)
									.append($("<div class='card-footer'>").text("Regular price: " + data[i].price +" RSD"))
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

function bookTicket(title, typeOfTicket){
	console.log( title + " " + typeOfTicket );
	dataForBooking = JSON.stringify({ "title": title, "typeOfTicket" : typeOfTicket});
	
	$.ajax({
        url:"/manifestations/bookTicket",
        method:"post",
        contentType: "application/json",
        data: dataForBooking,
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


