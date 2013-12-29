$(document).ready(function(){
	
	$('#download').click(function(e){
		console.log('download link');
		$.generateFile({
			filename	: 'export.txt',
			content		: $('textarea').val(),
			script		: 'download.php'
		});
		
		e.preventDefault();
	});
	
	$('#downloadPage').click(function(e){

		$.generateFile({
			filename	: 'page.html',
			content		: $('html').html(),
			script		: 'download.php'
		});
		
		e.preventDefault();
	});
	
});