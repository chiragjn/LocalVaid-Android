//window.onload = GetMap;

var map = null;
var searchManager = null;
var currInfobox = null;
var lat = 19.02;
var lng = 72.85;

function setLat(val) {
  lat = val;
}

function setLong(val) {
  lng = val;
}

function GetMap(){
	//Android.logMessage("GetMap", "Init");
     Microsoft.Maps.loadModule('Microsoft.Maps.Themes.BingTheme', { callback: function() 
         {
             map = new  Microsoft.Maps.Map(document.getElementById('divMap'), 
             { 
                credentials: "An9zbTfb9D1vPPusP1JVg2pl4s3ZgXQb-7z9gBbMybN3rNMoRnUNpV_4qpbOro6x",
                mapTypeId:  Microsoft.Maps.MapTypeId.road,
                showMapTypeSelector:false,
                showBreadcrumb: false,
                enableClickableLogo: false,
                enableSearchLogo: false,
                center: new  Microsoft.Maps.Location(lat, lng),
                zoom: 15,
                theme: new  Microsoft.Maps.Themes.BingTheme()
             });
             map.entities.clear(); 
         }
      });
      //Android.logMessage("GetMap", "done");
     Microsoft.Maps.loadModule('Microsoft.Maps.Search', {  callback: searchRequest });
  }
     
  function createSearchManager() {
     map.addComponent('searchManager', new  Microsoft.Maps.Search.SearchManager(map));
     searchManager = map.getComponent('searchManager');
  }

  function LoadSearchModule() {

     Microsoft.Maps.loadModule('Microsoft.Maps.Search', {  callback: searchRequest });
  }

  function searchRequest() {

     createSearchManager();
     var request =
         {
             query: "hospital",
             count: 10,
             startIndex: 0,
             bounds: map.getBounds(),
             callback: search_onHospitalSearchSuccess,
             errorCallback:  search_onSearchFailure
         };
     searchManager.search(request);
     request =
         {
             query: "pharmacy",
             count: 10,
             startIndex: 0,
             bounds: map.getBounds(),
             callback: search_onPharmacySearchSuccess,
             errorCallback:  search_onSearchFailure
         };
     searchManager.search(request);    
   }

  function search_onHospitalSearchSuccess(result, userData) {
     //map.entities.clear();
     //console.log("Success:     "+ result.searchResults);
     var searchResults = result && result.searchResults;
     if (searchResults) {
        //console.log(searchResults.length);
         for (var i = 0; i < searchResults.length; i++) {
             search_createHospitalMapPin(searchResults[i]);
         }
         if (result.searchRegion &&  result.searchRegion.mapBounds) {
             map.setView({ bounds:  result.searchRegion.mapBounds.locationRect });
         }
         else {
             alert('No results');
         }
      }
  }

  function search_onPharmacySearchSuccess(result, userData) {
     //map.entities.clear();
     //console.log("Success:     "+ result.searchResults);
     var searchResults = result && result.searchResults;
     if (searchResults) {
        //console.log(searchResults.length);
         for (var i = 0; i < searchResults.length; i++) {
             search_createPharmacyMapPin(searchResults[i]);
         }
         if (result.searchRegion &&  result.searchRegion.mapBounds) {
             map.setView({ bounds:  result.searchRegion.mapBounds.locationRect });
         }
         else {
             alert('No results');
         }
      }
  }

  function search_createHospitalMapPin(result) {
     if (result) {
        //console.log(result);
         var pin = new Microsoft.Maps.Pushpin(result.location, {icon : "img/h.png", width : 32 , height: 32, draggable: false, offset: new Microsoft.Maps.Point(0,16) });
         Microsoft.Maps.Events.addHandler(pin, 'click', function () {  
    search_showInfoBox(result) });
         map.entities.push(pin);
         //console.log(pin);
     }
  }

  function search_createPharmacyMapPin(result) {
     if (result) {
        //console.log(result);
         var pin = new Microsoft.Maps.Pushpin(result.location, {icon : "img/s.png", width : 32 , height: 32, draggable: false, offset: new Microsoft.Maps.Point(0,16)  });
         Microsoft.Maps.Events.addHandler(pin, 'click', function () {  
    search_showInfoBox(result) });
         map.entities.push(pin);
         //console.log(pin);
     }
  }

  function search_showInfoBox(result) {
     if (currInfobox) {
     currInfobox.setOptions({ visible: true });
     map.entities.remove(currInfobox);
     }
     currInfobox = new Microsoft.Maps.Infobox(
         result.location,
         {
             title: result.name,
             description: [result.address,  result.city, result.state, 
               result.country,  result.phone].join(' '),
             showPointer: true,
             titleAction: null,
             titleClickHandler: null 
         });
     currInfobox.setOptions({ visible: true });
     map.entities.push(currInfobox);
  }

  function search_onSearchFailure(result, userData) {
     alert('Search  failed');
  }