app.controller('AddMenu', ['$scope', '$rootScope', '$cookies', 'httpGetQuery', 'httpPostQuery', 'httpPutQuery', 'httpDeleteQuery', 'httpPostCloudinaryQuery', 'Upload', 'cloudinary', 'AuthService', function($scope, $rootScope, $cookies, httpGetQuery, httpPostQuery, httpPutQuery, httpDeleteQuery, httpPostCloudinaryQuery, $upload, cloudinary, AuthService) {
/**------------------------------------------------INITIAL-PART-------------------------------------------------------*/
    var validate = $cookies.get('validate'),
        restaurantId = $cookies.get('restaurant_Id'),
        accountId = $cookies.get("account_Id");
    $rootScope.restaurantCurrency = $cookies.get('restaurant_currency');
    if(validate === 'true') {
        var loggedUser = $cookies.get('logged_user');
        if(loggedUser) {
            $rootScope.loggedUser = loggedUser;
        }
        $rootScope.validate = true;
    }else {
        $rootScope.validate = false;
    }

    $rootScope.errorMessage = false;
    $rootScope.currentMenu = +$cookies.get('current_menu_Id') || '';
    $scope.loginPage = true;
    $scope.active = false;
    $scope.editMenu = false; //???
    $scope.menus = [];
/**-----------------------------------------------REFRESH-TOKEN-------------------------------------------------------*/
    AuthService.refreshToken();
/**-----------------------------------------------GET-ALL-DATA--------------------------------------------------------*/
    $scope.getMenusData = function () {
        var restaurantId = $cookies.get('restaurant_Id');
         if(restaurantId) {
             console.log('http://harristest.com.mocha6001.mochahost.com/foodme/restaurant/' + restaurantId + '/menus');
             $scope.restaurantDataPromiseGet = httpGetQuery.getData('http://harristest.com.mocha6001.mochahost.com/foodme/restaurant/' + restaurantId + '/menus');
             $scope.restaurantDataPromiseGet.then(function(value) {
                 console.log(value);
                 if(value.length === 0) {
                     $rootScope.currentMenu = '';
                 }
                 //console.log(value.menus);
                 $scope.menus = value;
                 var a;
                 for(a = $scope.menus.length; a--;) {
                     if(!$scope.menus[a].description) {
                         $scope.menus[a].description = 'Get people excited about your menu and your food. Give your menu a brief description';
                     }
                     $scope.menus[a].backupName = '';
                     $scope.menus[a].backupDescription = '';
                     $scope.menus[a].edit = false;
                     $scope.menus[a].backupDescription = '';
                     $scope.menus[a].backupDescription = '';
                     $scope.menus[a].internalId = a;
                     $scope.menus[a].collapsed = false;
                     var x;
                     for(x = $scope.menus[a].menuSections.length; x--;) {
                         $scope.menus[a].menuSections[x].backupName = '';
                         $scope.menus[a].menuSections[x].backupDescription = '';
                         $scope.menus[a].menuSections[x].edit = false;
                         $scope.menus[a].menuSections[x].active = false;
                         $scope.menus[a].menuSections[x].addPhoto = 'photo';
                         $scope.menus[a].menuSections[x].editDeletePhoto = false;
                         $scope.menus[a].menuSections[x].internalId = x;
                         $scope.menus[a].menuSections[x].collapsed = false;
                         if($scope.menus[a].menuSections[x].menuSectionPictureUrl) {
                             $scope.menus[a].menuSections[x].addPhoto = 'image';
                         }

                         var y;
                         for(y = $scope.menus[a].menuSections[x].dishes.length; y--;) {
                             $scope.menus[a].menuSections[x].dishes[y].availability = '';
                             $scope.menus[a].menuSections[x].dishes[y].backupName = '';
                             $scope.menus[a].menuSections[x].dishes[y].backupDescription = '';
                             $scope.menus[a].menuSections[x].dishes[y].edit = false;
                             $scope.menus[a].menuSections[x].dishes[y].active = false;
                             $scope.menus[a].menuSections[x].dishes[y].addPhoto = 'photo';
                             $scope.menus[a].menuSections[x].dishes[y].editDeletePhoto = false;
                             $scope.menus[a].menuSections[x].dishes[y].internalId = y;
                             if($scope.menus[a].menuSections[x].dishes[y].dishPictureUrl) {
                                 $scope.menus[a].menuSections[x].dishes[y].addPhoto = 'image';
                             }

                         }
                     }
                 }
             });
         }
     };
    $scope.getMenusData();
    //make possible to call this function from another scope
    //CallGetMenuDataMethod -- event name
    $rootScope.$on("CallGetMenuDataMethod", function(){
        console.log('emit works fine');
        $scope.getMenusData();
    });

    $scope.check = function() {
        console.log($scope.menus[0].id);
        console.log($rootScope.currentMenu);
    };
/**--------------------------------------------------UNIVERSAL-ACTIONS------------------------------------------------*/
    $scope.editAction = function(k) {
        console.log(k.name);
        k.edit = true;
        k.backupName = k.name;
        k.backupDescription = k.description;
    };
    $scope.deleteAction = function(k, item) {
        $scope.dataPromiseDelete = httpDeleteQuery.deleteData('http://harristest.com.mocha6001.mochahost.com/foodme/' + item + '/' + k.id );
        $scope.dataPromiseDelete.then(function(value) {
            console.log('Object is deleted');
            $scope.getMenusData();
        });
    };
    $scope.closeSaving = function(k) {
        k.edit = false;
        k.name = k.backupName;
        k.description = k.backupDescription;
    };
    $scope.saveChanges = function(k, item) {
        k.edit = false;
        console.log(angular.toJson(k));
        console.log(k);
        $scope.dataPromisePut = httpPutQuery.putData('http://harristest.com.mocha6001.mochahost.com/foodme/' + item + '/' + k.id , k);
        $scope.dataPromisePut.then(function(value) {
            console.log('Object is updated, reply is:');
            console.log(value);
            console.log(angular.toJson(value));
            $scope.getMenusData();
        });
    };
/**----------------------------------------------------MENU-ACTIONS---------------------------------------------------*/
    $scope.expandCollapseAllSections = function(a, action) {
        if(action === 'expand') {
            a.collapsed = false;
            var i;
            for (i =  a.menuSections.length; i--;) {
                a.menuSections[i].collapsed = false;
            }
        } else if(action === 'collapse') {
            a.collapsed = true;
            for (i =  a.menuSections.length; i--;) {
                a.menuSections[i].collapsed = true;
            }
        }

    };
    $scope.addNewSection = function(a) {
        /*backupName: '', backupDescription: '', edit: true, active: false, addPhoto: 'photo', editDeletePhoto: false, menuSectionPictureUrl: '',
         internalId: a.menuSections.length, collapsed: false,*/
        var newSections = {name: 'A New Section', description: 'Get people excited about your menu and your food.'}; //, dishes: []
        console.log(angular.toJson(newSections));
        //a.menuSections.push(newSections);
        console.log(a);
        console.log('http://harristest.com.mocha6001.mochahost.com/foodme/menu/' + a.id + '/menusection');
        $scope.sectionDataPromisePost = httpPostQuery.postData('http://harristest.com.mocha6001.mochahost.com/foodme/menu/' + a.id + '/menusection', newSections);
        $scope.sectionDataPromisePost.then(function(value) {
            console.log(value);
            $scope.getMenusData();
            //$scope.menus[a.id].menuSections[a.menuSections.length - 1].edit = true;
            console.log($scope.menus);
        });
    };
/**-------------------------------------------------SECTION-ACTIONS---------------------------------------------------*/
    $scope.expandCollapseSection = function(a, x, action) {
        if(action === 'expand') {
            x.collapsed = false;
            var i;
            for (i = a.menuSections.length; i--;) {
                if(a.menuSections[i].collapsed === false) {
                    if(i === 0) {
                        a.collapsed = false;
                    }
                } else break;

            }
        } else if(action === 'collapse') {
            x.collapsed = true;
            for (i = a.menuSections.length; i--;) {
                if(a.menuSections[i].collapsed === true) {
                    if(i === 0) {
                        a.collapsed = true;
                    }
                } else break;

            }
        }
    };
    $scope.addNewDish = function(x) {
        /*backupName: '', backupDescription: '', edit: true, active: false, addPhoto: 'photo', editDeletePhoto: false, dishPictureUrl: '',
         internalId: a.menuSections.length, collapsed: false, */
        var newDish = {name: 'A New Dish', description: 'Tasty dish.'};
        //a.menuSections.push(newSections);
        console.log(x);
        $scope.sectionDataPromisePost = httpPostQuery.postData('http://harristest.com.mocha6001.mochahost.com/foodme/menusection/' + x.id + '/dish', newDish);
        $scope.sectionDataPromisePost.then(function(value) {
            console.log(value);
            $scope.getMenusData();
            //$scope.menus[a.id].menuSections[a.menuSections.length - 1].edit = true;
            console.log($scope.menus);
        });
    };
/*------------------------------------------------Section Image-------------------------------------------------------*/
    $scope.showPhoto = function(x) {
        if( x.addPhoto !== 'image') {
            x.addPhoto = 'photo'
        }else {
            x.editDeletePhoto = false;
        }
        //console.log(x.addPhoto);
    };
    $scope.showPlus = function(x) {
        if( x.addPhoto !== 'image') {
            x.addPhoto = 'plus'
        }else {
            x.editDeletePhoto = true;
        }
        //console.log(x.addPhoto);
    };
    $scope.showImage = function(x) {
        x.addPhoto = 'image'
    };
/**--------------------------------------------------Adding Image-----------------------------------------------------*/
    var handleFileSelect = function (evt) {
        var d = new Date();
        $scope.title = "Image (" + d.getDate() + " - " + d.getHours() + ":" + d.getMinutes() + ":" + d.getSeconds() + ")";
        var currentMenuInternalId = evt.data.a,
            currentSectionInternalId = evt.data.x,
            currentDishInternalId = evt.data.y,
            file = evt.currentTarget.files[0];
        /**reader = new FileReader(); //-- in case of use the 64bit image coding*/

        if (file && !file.$error) {
            file.upload = $upload.upload({
                url: "https://api.cloudinary.com/v1_1/" + cloudinary.config().cloud_name + "/upload",
                data: {
                    upload_preset: cloudinary.config().upload_preset,
                    tags: 'myphotoalbum',
                    context: 'photo=' + $scope.title,
                    file: file
                },
                headers: {
                    Authorization: undefined
                }
            }).progress(function (e) {
                //file.progress = Math.round((e.loaded * 100.0) / e.total);
                //file.status = "Uploading... " + file.progress + "%";
            }).success(function (data, status, headers, config) {
                $rootScope.photos = $rootScope.photos || [];
                data.context = {custom: {photo: $scope.title}};
                file.result = data;
                //$rootScope.photos.push(data);
                $scope.displayPhoto(file.result);
            }).error(function (data, status, headers, config) {
                file.result = data;
            });
        }

        $scope.displayPhoto = function(fileResult) {
            if(currentDishInternalId !== undefined) {
                $scope.menus[currentMenuInternalId].menuSections[currentSectionInternalId].dishes[currentDishInternalId].dishPictureUrl = fileResult.url; //evt.target.result
                var myDish = $scope.menus[currentMenuInternalId].menuSections[currentSectionInternalId].dishes[currentDishInternalId];
                console.log(myDish);
                $scope.showImage(myDish);
                $scope.saveChanges(myDish, 'dish');
            }else {
                console.log(fileResult.url);
                $scope.menus[currentMenuInternalId].menuSections[currentSectionInternalId].menuSectionPictureUrl = fileResult.url;
                var mySection = $scope.menus[currentMenuInternalId].menuSections[currentSectionInternalId];
                console.log(mySection);
                $scope.showImage(mySection);
                $scope.saveChanges(mySection, 'menusection');
            }
            //console.log($scope.menus);
        };



    /** //-- in case of use the 64bit image coding
     reader.onload = function (evt) {
            $scope.$apply(function ($scope) {
                if(evt.target.result) {

                }
            });
        };
     reader.readAsDataURL(file);
     */

    };
    $scope.attachEventOnSection = function(a, x) {
        console.log(this.x);
        console.log(angular.element(angular.element('.sectionImgInput')[x.internalId])[0]);
        angular.element(angular.element('.sectionImgInput')[x.internalId]).on('change', {a: a.internalId, x: x.internalId}, handleFileSelect);
    };
    $scope.attachEventOnDish = function(a, x, y) {
        var currentElement = angular.element(angular.element(angular.element(angular.element(angular.element(angular.element('.section')[x.internalId]).children()[y.internalId+1]).children().children().children()[0]).children().children()[1]).children().children());
        console.log(currentElement);
        currentElement.on('change', {a: a.internalId, x: x.internalId, y: y.internalId}, handleFileSelect);
    };
/**-------------------------------------------------DELETE-IMAGE------------------------------------------------------*/
    $scope.deletePhoto = function(x, item) {
        if(item === 'menusection') {
            x.menuSectionPictureUrl = '';
        } else if(item === 'dish') {
            x.dishPictureUrl = '';
        }
        x.addPhoto = 'photo';
        $scope.saveChanges(x, item);
    };


}]);


