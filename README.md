# Stylus

Down, up & move events with coordinates and pressure data from pen / stylus for your cordova project. 

Currently only supports Android but looking to support more platforms in the future. 

## Usage
```JavaScript
if (window.StylusPlugin) {
  StylusPlugin.registerListeners();
}

document.addEventListener('stylusplugin-up', event => {
  console.dir(event);
});
document.addEventListener('stylusplugin-down', event => {
  console.dir(event);
});
document.addEventListener('stylusplugin-move', event => {
  console.dir(event);
});
```

The event object looks like this:

```TypeScript
{
  "x": number,
  "y": number,
  "pressure": number
}
```