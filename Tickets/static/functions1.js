function logOut(){
	console.log("log out");
				$.ajax({
							url: "/logout",
							method: "get",
							complete: function(){
								location.href="/"
							}   
						})
			}
