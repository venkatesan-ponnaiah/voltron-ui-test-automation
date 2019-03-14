
cell.each(function() {
var cell_value = $(this).html();
console.log("Cell value is " + cell_value);
if (cell_value == PASS) {
    $(this).css({'background' : 'GREEN'});   
} else if (cell_value == FAIL) {
    $(this).css({'background' : 'RED'});
} 
});