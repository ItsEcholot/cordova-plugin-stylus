var StylusPlugin = function () {
  console.log('StylesPlugin starting up');
};

StylusPlugin.prototype.registerListeners = function () {
  cordova.exec(data => {
    switch (data.type) {
      case "up":
        cordova.fireDocumentEvent('stylusplugin-up', data);
        break;
      case "down":
        cordova.fireDocumentEvent('stylusplugin-down', data);
        break;
      case "move":
        cordova.fireDocumentEvent('stylusplugin-move', data);
        break;
    }
  }, (err) => {
    callback(err);
  }, 'Stylus', 'registerListeners', []);
}

module.exports = new StylusPlugin();
