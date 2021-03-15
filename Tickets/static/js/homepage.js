
	function getManifestations(){
		$.ajax({
            url:"/allManifestations",
            method:"get",
            dataType: "JSON",
            success:function(data){
				console.log(data);
				var i = 0;
				for(i; i<data.length; i++){

					var date = new Date((data[i].realisationDate));
					$("#cards")
					.append($("<div class='col-md-4 div-space'>")
							.append($("<div class='card'>")
									.append($("<div class='card-header'>").append($("<h5>").text(data[i].manifestationType)))
									.append($("<img src='"+data[i].posterPath+"'>"))
									.append($("<div class='card-body text-center'>")
											.append($("<div class='card-title'>").append($("<h5>").text(data[i].title)))
											.append($("<p class='card-text'>").text(date.toLocaleString("sr-sp")))
											.append($("<p class='card-text'>").text(data[i].location.street +" " + data[i].location.number + ", " + data[i].location.city))
										
											)
									.append($("<div class='card-footer'>").text("Price: " + data[i].price +" RSD"))
											));
						
				
            }
            }
		});
		}