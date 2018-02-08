app.service('dataSer', function() {
    var dataList = [];
    var addData = function(newObj) {
        dataList = [];
        dataList.push(newObj);
    };
    var getData = function(){
        return dataList;
    };
    return {
        addData: addData,
        getData: getData
    };
});
app.service('dataSer1', function() {
    var dataList = [];
    var addData = function(newObj) {
        dataList = [];
        dataList.push(newObj);
    };
    var getData = function(){
        return dataList;
    };
    return {
        addData: addData,
        getData: getData
    };
});