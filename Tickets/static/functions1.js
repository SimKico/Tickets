function logOut(){
	console.log("log out");
	localStorage.removeItem('role');
				$.ajax({
							url: "/logout",
							method: "get",
							complete: function(){
								location.href="/"
							}   
						})
			}
			
function homepage(){
		var role = localStorage.getItem("role");
		if(role === "ADMIN"){
			location.href = "homepageAdmin.html";
		}else if(role === "SELLER"){
			location.href = "homepageSeller.html";
		}else{
			location.href = "homepageBuyer.html";
		}
}
