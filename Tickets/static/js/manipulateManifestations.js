function checkRole() {
	if(localStorage.getItem('role') !== 'SELLER'){
		goToHomepage();
	}
}

function getSellerManifestations() {
	if(localStorage.getItem('role') !== 'SELLER'){
		goToHomepage();
	}
	var username = localStorage.getItem('username');
	localStorage.removeItem("resultManifestation");
	localStorage.removeItem("dataForSort");
	$.ajax({
		url: "/manifestations/" + username,
		method: "get",
		dataType: "JSON",
		success: function (data) {
			console.log(data);
			var i = 0;
			for (i; i < data.length; i++) {
				var date = new Date(data[i].realisationDate);
				
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
			
		},
	});
}



function add(e){
	e.preventDefault();
	var title = $('#title').val();
	var seats = $('#seats').val();
	var price = $('#price').val();
	var street = $('#street').val();
	var number = $('#number').val();
	var city = $('#city').val();
	var zipCode = $('#zipCode').val();
	var fileReader = new FileReader();
	   
    fileReader.readAsDataURL($('#poster').prop('files')[0]);
    
    var data = fileReader.result;
    var image;
	var poster = $('#poster')[0].files[0];
	if (poster) {
	    var reader = new FileReader();
	    reader.readAsDataURL(poster);
	    reader.onload = function(e) {
	        // browser completed reading file - display it
	       image = e.target.result;
	    };
	}
	var date = $("#datepicker").val();
	var time = $("#time option:selected").text();
	var type = $("#type option:selected").val();

	
	$.ajax({
		url: "https://nominatim.openstreetmap.org/search/"+street+"%20"+number+"%20"+city+"?format=json&addressdetails=1&limit=1&polygon_svg=1",
		method: "get",
		dataType: "JSON",
		success: function (data) {
			console.log(data[0].lat);
			console.log(data[0].lon);
		    newManifestation = JSON.stringify({title: title, seats: seats, type: type, price: price, date: date, time: time, price: price, street: street, number: number, city: city, zipCode: zipCode, lat: data[0].lat, lon: data[0].lon, poster: image});
			$.ajax({
				url: "/manifestations",
				method: "post",
				contentType: "application/json",
				data: newManifestation,
				dataType: "JSON",
				success: function (data) {
					
				},
				error: function(res){
					console.log(res)
				    alert(res.responseJSON.message);
				     
				  }
			});
			},
		error : function(){
			alert("Invalid address. Please check again or try without a number. ")
		}
		});
}

$(document).ready(function(){
	
	$('form').on('submit', add);
	
    $("#price").on("input", function(){
    	var toPrice = $("#price").val();
    	
    	if(toPrice < 500 || toPrice > 3000){
			$("#price").css('background-color',"rgba(255,0,0,0.5)");
			$("#add").prop("disabled",true);
		}else{
			$("#price").css('background-color', "#FFFFFF");
			$("#add").prop("disabled",false);
		}
    });
    $("#number").on("input", function(){
    	var value = $("#number").val();
    	if(value < 0){
			$("#number").css('background-color', "rgba(255,0,0,0.5)");
			$("#add").prop("disabled",true);
		}else{
			$("#number").css('background-color', "#FFFFFF");
			$("#add").prop("disabled",false);
			}
    });
    $("#seats").on("input", function(){
    	var value = $("#seats").val();
    	if(value < 100 || value > 15000){
			$("#seats").css('background-color', "rgba(255,0,0,0.5)");
			$("#add").prop("disabled",true);
		}else{
			$("#seats").css('background-color', "#FFFFFF");
			$("#add").prop("disabled",false);
			}
    	
    });


    
});
