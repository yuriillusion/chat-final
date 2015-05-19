var uniqueId = function() {
	var date = Date.now();
	var random = Math.random() * Math.random();

	return Math.floor(date * random).toString();
};

var createMessage = function(author, text){
  return {
    author: author,
    text: text,
    id: uniqueId(),
    statusCode: 0
  };
};

var appState = {
	mainUrl : 'http://localhost:8080/chat',//'chat',
	messagesList:[],
	token : 'TE11EN'
};

var username = 'Default';

var editingWrap = null;

function run(){
	var appContainer = document.getElementsByClassName('container')[0];
	changeUsername(username);
	appContainer.addEventListener('click', delegateEvent);
	restore();
}

function delegateEvent(evtObj) {
	if(evtObj.type === 'click'){
	  if(editingWrap !== null && !evtObj.target.classList.contains('message-edit-panel')){
	    finishEditing();
	  }
	  if(evtObj.target.classList.contains('change-button')){
		  onChangeButtonClick();
	  } else if(evtObj.target.classList.contains('send-button')){
	    onSendButtonClick();
	  } else if(evtObj.target.classList.contains('delete-icon')){
	    onDeleteIconClick(evtObj.target.parentNode.parentNode);
	  } else if(evtObj.target.classList.contains('edit-icon')){
	    onEditIconClick(evtObj.target.parentNode.parentNode);
	  }
	}
}

function refreshAllMessageWraps(messagesList){
  var messagesArea = document.getElementsByClassName('messages-area')[0];
  while(messagesArea.children.length > 0){
    remove(messagesArea.firstChild);
  }
  for(var index in messagesList){
    messagesArea.appendChild(createMessageWrap(messagesList[index]));
	}
}

function onChangeButtonClick(){
	var nameEdit = document.getElementsByClassName('nickname-edit')[0];
	if(nameEdit.value){
	  changeUsername(nameEdit.value);
	  nameEdit.value = '';
	} else {
	  window.alert("Nickname may not be empty.");
	}
}

function onSendButtonClick(){
  var messageEdit = document.getElementById('message-edit');
  if(messageEdit.value){
    var text = messageEdit.value;
    text = text.replace(/\n/g, "\\n");
    var message = createMessage(username, text);
    messageEdit.value = '';
    addMessage(message);
  }
}

function onEditIconClick(target){
  editingWrap = target;
  remove(target.lastChild);
  var editPanel = document.createElement('textarea');
  editPanel.classList.add('message-edit-panel');
  var id = target.attributes['message-wrap-id'].value;
  var message = getMessageById(id);
  var textNode = document.createTextNode(message.text);
  editPanel.appendChild(textNode);
  editingWrap.appendChild(editPanel);
}

function finishEditing(){
  var text = editingWrap.lastChild;
  var id = editingWrap.attributes['message-wrap-id'].value;
  var message = getMessageById(id);
  message.text = text.value;
  message.statusCode = 1;
  editingWrap = null;
  
  var data = "{\"message\":" + JSON.stringify(message) + "}";
  put(appState.mainUrl, data, function(){
    restore();
  });
}

function onDeleteIconClick(target){
  var id = target.attributes['message-wrap-id'].value;
  remove(target);
  var url = appState.mainUrl + '?id=' + id;
  delete_(url, function(){
    restore();
  });
}

function addMessage(message, continueWith){
  var data = "{\"message\": " + JSON.stringify(message) + "}";
  post(appState.mainUrl, data, function(){
    restore();
  });
}

function getMessageById(id){
  var messagesList = appState.messagesList;
  for(var index in messagesList){
    if(messagesList[index].id == id){
      return messagesList[index];
    }
  }
  return null;
}

function createMessageWrap(message){
  var messageWrap = document.createElement("div");
  messageWrap.classList.add("message");

  var messageTop = document.createElement("div");
  messageTop.classList.add("top");

  var nicknameField = document.createElement("p");
  nicknameField.classList.add("nickname");
  var nicknameNode = document.createTextNode(message.author);
  nicknameField.appendChild(nicknameNode);

  messageTop.appendChild(nicknameField);

  if(message.author === username && message.statusCode < 2){
    var editIcon = document.createElement("img");
    editIcon.setAttribute("src", "http://s1.iconbird.com/ico/0612/GooglePlusInterfaceIcons/w18h181338911463pencil.png");
    editIcon.classList.add("icon");
    editIcon.classList.add('edit-icon');

    var deleteIcon = document.createElement("img");
    deleteIcon.setAttribute('src', "http://s1.iconbird.com/ico/0612/GooglePlusInterfaceIcons/w18h181338911480close.png");
    deleteIcon.classList.add('icon');
    deleteIcon.classList.add('delete-icon');
    messageTop.appendChild(deleteIcon);
    messageTop.appendChild(editIcon);
  }

  var textField = document.createElement("pre");
  textField.classList.add("text");
  var textNode = document.createTextNode(message.text);
  if(message.statusCode == 2){
    textField.classList.add("info");
  }
  textField.appendChild(textNode);
  if(message.statusCode == 1){
    var infoField = document.createElement("div");
    infoField.classList.add("info");
    var infoNode = document.createTextNode("redacted");
    infoField.appendChild(infoNode);
    textField.appendChild(infoField);
  }
  
  messageWrap.appendChild(messageTop);
  messageWrap.setAttribute('message-wrap-id', message.id);
  messageWrap.appendChild(textField);

  return messageWrap;
}

function changeUsername(newUsername){
  document.getElementById('nickname-current').innerHTML = newUsername;
  username = newUsername;
  refreshAllMessageWraps(appState.messagesList);
}

function remove(element){
  element.parentNode.removeChild(element);
}

function restore(continueWith){
  var url = appState.mainUrl + '?token=TN11EN';//appState.token;
  
  get(url, function(responseText) {
		console.assert(responseText !== null);

		var response = JSON.parse(responseText);

		appState.token = response.token;
		
		var messagesList = [];
		var jsonMessagesList = response.messages;
		for(var index in jsonMessagesList){
		  var jsonMessage = jsonMessagesList[index];
		  var message = {
            author : jsonMessage.author,
		    text : jsonMessage.text,
		    id : jsonMessage.id,
		    statusCode : jsonMessage.statusCode
		  };
		  messagesList.push(message);
		}  
		appState.messagesList = messagesList;
		refreshAllMessageWraps(messagesList);

		continueWith && continueWith();
	});
}

function get(url, continueWith, continueWithError) {
	ajax('GET', url, null, continueWith, continueWithError);
}

function post(url, data, continueWith, continueWithError) {
	ajax('POST', url, data, continueWith, continueWithError);	
}

function delete_(url, continueWith, continueWithError) {
  ajax('DELETE', url, null, continueWith, continueWithError);
}

function put(url, data, continueWith, continueWithError) {
	ajax('PUT', url, data, continueWith, continueWithError);	
}

function ajax(method, url, data, continueWith, continueWithError) {
	var xhr = new XMLHttpRequest();

	continueWithError = continueWithError || defaultErrorHandler;
	xhr.open(method || 'GET', url, true);

	xhr.onload = function () {
		if (xhr.readyState !== 4)
			return;

		if(xhr.status != 200) {
			continueWithError('Error on the server side, response ' + xhr.status);
			return;
		}

		if(isError(xhr.responseText)) {
			continueWithError('Error on the server side, response ' + xhr.responseText);
			return;
		}

		continueWith(xhr.responseText);
	};    

    xhr.ontimeout = function () {
    	continueWithError('Server timed out !');
    };

    xhr.onerror = function (e) {
    	var errMsg = 'Server connection error !\n'+
    	'\n' +
    	'Check if \n'+
    	'- server is active\n'+
    	'- server sends header "Access-Control-Allow-Origin:*"';

        continueWithError(errMsg);
    };

    xhr.send(data);
}

function isError(text) {
	if(text === "")
		return false;
	
	try {
		var obj = JSON.parse(text);
	} catch(ex) {
		return true;
	}

	return !!obj.error;
}

function defaultErrorHandler(message) {
	console.error(message);
	output(message);
}