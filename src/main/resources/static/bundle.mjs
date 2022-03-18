let main = {exports:{}};
var script;

if (typeof fetch === 'undefined') {
  await import('path').then(path => globalThis.__dirname = path.dirname(import.meta.url).replace('file:/', ''));
  script = await import('fs').then(fs => fs.readFileSync(__dirname + '/classes.js', {encoding: 'utf8'}));
} else {
  script = await fetch('./classes.js').then(response => response.text());
}

Function('return function(module, exports) {\n' +
  script
  + '\nmain(); module.exports = main.exported;}')()(main, main.exports);

let hello = (...args) => main.exports.hello(args);
let greet = () => main.exports.greet();

export {greet, hello};
export default greet;