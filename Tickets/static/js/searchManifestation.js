	function readSearchResult(){
		if(localStorage.getItem('resultManifestation') !== null){
			console.log(localStorage.getItem('resultManifestation'));
			var data = JSON.parse(localStorage.getItem('resultManifestation')); 
			localStorage.setItem('dataForSort', JSON.stringify(data));
			showSearchResult(data);
			showFilter(data);
		}else{
			alert("There are no active searches!");
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
		
	}

	function showSearchResult(data){
		
		var i = 0;
		for(i; i<data.length; i++){
			var rating = data[i].averageRating === 0 ? "Not held" : data[i].averageRating;
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
							.append($("<div class='card-footer'>").text("Price: " + data[i].price +" RSD")
																  .append($("<p class='card-text p_text'>").text("Rating: " + rating)))
									));
				
		
       }		
	
	}

	function showFilter(data){
		var i = 0;
		var options = [];
		if(data.length > 0){
			 $("#filterByType")
			   .append($("<p class='mb-1 mr-sm-2'>").text("By manifestation type: "));
			$("#typeFilter")
			   .append($("<button class='btn search_btn mb-1' onclick='filter()'>").text("Apply"))
			   .append($("<button id='removeTypeFilter' class='btn search_btn mb-1'  hidden='true' onclick='removeTypeFilter()'>").text("Reset"));
			
		}
		for(i; i<data.length; i++){
			
			if(!options.includes(data[i].manifestationType))
			{	
				options.push(data[i].manifestationType);
				$("#filterByType")
				   .append($("<div id='typeFilter' class='mb-1 mr-sm-2'>").append($("<input id='manType' type='radio' name='choiceType' value='"+data[i].manifestationType+"' >"))
						   							  .append($("<a>").text(data[i].manifestationType)));
			}
		}
		
		
	}

	function sortAsc(criteria){
		var manifestations = JSON.parse(localStorage.getItem('dataForSort'));
		data = JSON.stringify({manifestations : manifestations, criteria : criteria});
		console.log(data);
		$.ajax({
            url:"/manifestations/sortAsc",
            method:"post",
            contentType: "application/json",
            data: data,
            dataType: "JSON",
            success:function(data){
                console.log(data);
                $( "#cards" ).empty();
                showSearchResult(data);
                
            },
            error: function(xhr, textStatus, error){
                console.log(xhr.statusText);
                console.log(textStatus);
                console.log(error);
            }
		});
		}

	function sortDesc(criteria){
		var manifestations = JSON.parse(localStorage.getItem('dataForSort'));
		data = JSON.stringify({manifestations : manifestations, criteria : criteria});
		console.log(data);
		$.ajax({
            url:"/manifestations/sortDesc",
            method:"post",
            contentType: "application/json",
            data: data,
            dataType: "JSON",
            success:function(data){
                console.log(data);
                $( "#cards" ).empty();
                showSearchResult(data);  
            },
            error: function(xhr, textStatus, error){
                console.log(xhr.statusText);
                console.log(textStatus);
                console.log(error);
            }
		});		
	}

	function filter(){
		var type = $("input[name=choiceType]:checked").val() === undefined ? "none" : $("input[name=choiceType]:checked").val();
		var available = $("input[name=choiceStatus]:checked").val() === "available" ? true : false;

		var manifestations = JSON.parse(localStorage.getItem('resultManifestation'));
		data = JSON.stringify({manifestations : manifestations, type : type, available : available});

		$.ajax({
            url:"/manifestations/filter",
            method:"post",
            contentType: "application/json",
            data: data,
            dataType: "JSON",
            success:function(data){
            	if(type != "none")
                	$("#removeTypeFilter").prop('hidden', false);
                if(available == true)
                	$("#removeFilter").prop('hidden', false);
            	
                console.log(data);
                localStorage.setItem('dataForSort', JSON.stringify(data));
                $( "#cards" ).empty();
                showSearchResult(data);  
            },
            error: function(xhr, textStatus, error){
                console.log(xhr.statusText);
                console.log(textStatus);
                console.log(error);
            }
		});		
		
		
	}

	function removeTypeFilter(){
		$("input[name=choiceType]:checked").prop('checked', false);
		$("#removeTypeFilter").prop('hidden', true);
		if($("input[name=choiceStatus]:checked").val() == "available"){
			filter();
		}else{
			$( "#cards" ).empty();
			var data = JSON.parse(localStorage.getItem('resultManifestation'));
			localStorage.setItem('dataForSort', JSON.stringify(data));
			showSearchResult(data);
		}
		
	}

	function removeAvailableFilter(){
		$("input[name=choiceStatus]:checked").prop('checked', false);
		$("#removeFilter").prop('hidden', true);
		if($("input[name=choiceType]:checked") !== undefined){
			filter();
		}else{
			$( "#cards" ).empty();
			var data = JSON.parse(localStorage.getItem('resultManifestation'));
			localStorage.setItem('dataForSort', JSON.stringify(data));
			showSearchResult(data);
		}
	}

	function clearSearch(){
	
		localStorage.removeItem("resultManifestation");
		localStorage.removeItem("dataForSort");	
		console.log(localStorage.getItem('role'));
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