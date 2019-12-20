var StylusPlugin = function () {
  console.log('StylesPlugin starting up');
};

StylusPlugin.prototype.registerListeners = function () {
  cordova.exec(data => {
    switch (data.type) {
      case 'stylusplugin-down':
        cordova.fireDocumentEvent('stylusplugin-down', data);
        break;
      case 'stylusplugin-up':
        cordova.fireDocumentEvent('stylusplugin-up', data);
        break;
      case 'stylusplugin-move':
        cordova.fireDocumentEvent('stylusplugin-move', data);
        break;
      case 'stylusplugin-unknown':
        // What are we doing with this stuff?
        break;
    }
  }, (err) => {
    callback(err);
  }, 'Stylus', 'registerListeners', []);
}

module.exports = new StylusPlugin();