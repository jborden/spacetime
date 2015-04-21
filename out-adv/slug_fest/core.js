// Compiled by ClojureScript 0.0-3126 {:optimize-constants true, :static-fns true}
goog.provide('slug_fest.core');
goog.require('cljs.core');
slug_fest.core.initial_time = (function (){var G__10672 = null;
return (cljs.core.atom.cljs$core$IFn$_invoke$arity$1 ? cljs.core.atom.cljs$core$IFn$_invoke$arity$1(G__10672) : cljs.core.atom.call(null,G__10672));
})();
slug_fest.core.hero_texture = (new THREE.ImageUtils.loadTexture("resources/images/run.png"));
slug_fest.core.hero_texture_current_tile = (0);
slug_fest.core.hero_texture_current_tile_display_time = (0);
slug_fest.core.hero_texture_tile_display_duration = (500);
/**
 * Change the frame of a texture composed of a total of num-tiles with a corresponding amount of horizontal-tiles and vertical-tiles and frame current-frame. This function modifies the textures x and y offset is modified. Returns (inc current-frame).
 */
slug_fest.core.increment_texture_animation_frame_BANG_ = (function slug_fest$core$increment_texture_animation_frame_BANG_(texture,horizontal_tiles,vertical_tiles,num_tiles,current_frame){
var current_frame__$1 = (((current_frame > num_tiles))?(0):current_frame);
var current_column = cljs.core.mod(current_frame__$1,horizontal_tiles);
var current_row = (function (){var G__10674 = (current_frame__$1 / horizontal_tiles);
return Math.floor(G__10674);
})();
texture.offset.x = (current_column / horizontal_tiles);

texture.offset.y = (current_row / vertical_tiles);

return (current_frame__$1 + (1));
});
slug_fest.core.scene = (new THREE.Scene());
/**
 * Create a THREE.Scene object.
 * see:http://threejs.org/docs/#Reference/Scenes/Scene
 */
slug_fest.core.create_scene = (function slug_fest$core$create_scene(){
return (new THREE.Scene());
});
slug_fest.core.camera = (function (){var camera = (new THREE.PerspectiveCamera((45),(window.innerWidth / window.innerHeight),0.1,(20000)));
slug_fest.core.scene.add(camera);

camera.position.set((0),(150),(400));

camera.lookAt(slug_fest.core.scene.position);

return camera;
})();
/**
 * Create a THREE.PerspectiveCamera with camera frustrum fov (field of view), aspect (aspect ratio),
 * near (near plane) and far (far plane).
 * see: http://threejs.org/docs/#Reference/Cameras/PerspectiveCamera
 */
slug_fest.core.create_perspective_camera = (function slug_fest$core$create_perspective_camera(fov,aspect,near,far){
return (new THREE.PerspectiveCamera(fov,aspect,near,far));
});
/**
 * Create a THREE.WebGLRenderer with js-obj parameters.
 * Example usage: (create-webgl-renderer (js-obj "antialias" true))
 * see: http://threejs.org/docs/#Reference/Renderers/WebGLRenderer
 */
slug_fest.core.create_webgl_renderer = (function slug_fest$core$create_webgl_renderer(parameters){
return (new THREE.WebGLRenderer(parameters));
});
slug_fest.core.renderer = (function (){var renderer = (cljs.core.truth_(Detector.webgl)?(new THREE.WebGLRenderer((function (){var obj10676 = {"antialias":true};
return obj10676;
})())):(new THREE.CanvasRender()));
renderer.setSize(window.innerWidth,window.innerHeight);

return renderer;
})();
slug_fest.core.container = (function (){var container = document.getElementById("ThreeJS");
container.appendChild(slug_fest.core.renderer.domElement);

return container;
})();
slug_fest.core.controls = (new THREE.OrbitControls(slug_fest.core.camera,slug_fest.core.renderer.domElement));
slug_fest.core.stats = (function (){var stats = (new Stats());
stats.domElement.style.position = "absolute";

stats.domElement.style.bottom = "0px";

stats.domElement.style.zIndex = (100);

return stats;
})();
slug_fest.core.light = (function (){var light = (new THREE.PointLight((16777215)));
light.position.set((100),(250),(100));

return light;
})();
/**
 * Create a light with hexadecimal color at coordinates x,y, and z
 */
slug_fest.core.create_light = (function slug_fest$core$create_light(color,x,y,z){
var light = (new THREE.PointLight(color));
light.position.set(x,y,z);

return light;
});
slug_fest.core.floor_texture = (function (){var texture = (new THREE.ImageUtils.loadTexture("resources/images/checkerboard.jpg"));
(texture["wrapS"] = THREE.RepeatWrapping);

(texture["wrapT"] = THREE.RepeatWrapping);

texture.repeat.set((10),(10));

return texture;
})();
/**
 * Create a THREE.Texture using image tiled U by V times.
 * see: http://threejs.org/docs/#Reference/Extras/ImageUtils
 * http://threejs.org/docs/#Reference/Textures/Texture
 */
slug_fest.core.create_tiled_texture = (function slug_fest$core$create_tiled_texture(image_str,U,V){
var texture = (new THREE.ImageUtils.loadTexture(image_str));
(texture["wrapS"] = THREE.RepeatWrapping);

(texture["wrapT"] = THREE.RepeatWrapping);

texture.repeat.set(U,V);

return texture;
});
slug_fest.core.floor_material = (new THREE.MeshBasicMaterial((function (){var obj10678 = {"map":slug_fest.core.floor_texture,"side":THREE.DoubleSide};
return obj10678;
})()));
slug_fest.core.floor_geometry = (new THREE.PlaneGeometry((1000),(1000),(10),(10)));
/**
 * Create a THREE.PlaneGeometry using width, height, width-segments and height-segments.
 * see:http://threejs.org/docs/#Reference/Extras.Geometries/PlaneGeometry
 */
slug_fest.core.create_plane_geometry = (function slug_fest$core$create_plane_geometry(width,height,width_segments,height_segments){
return (new THREE.PlaneGeometry(width,height,width_segments,height_segments));
});
/**
 * Create a box geometry that of width, height and depth.
 * see: http://threejs.org/docs/#Reference/Extras.Geometries/BoxGeometry
 */
slug_fest.core.create_box_geometry = (function slug_fest$core$create_box_geometry(width,height,depth){
return (new THREE.BoxGeometry(width,height,depth));
});
slug_fest.core.skybox_geometry = (new THREE.BoxGeometry((10000),(10000),(10000)));
slug_fest.core.skybox_material = (new THREE.MeshBasicMaterial((function (){var obj10680 = {"color":(10066431),"side":THREE.BackSide};
return obj10680;
})()));
/**
 * Create a THREE.MeshBasicMaterial object using the js-obj parameters.
 * Example usage: (create-mesh-basic-material (js-obj "color" 0x9999ff "side" js/THREE.BackSide))
 * see: http://threejs.org/docs/#Reference/Materials/MeshBasicMaterial
 */
slug_fest.core.create_mesh_basic_material = (function slug_fest$core$create_mesh_basic_material(parameters){
return (new THREE.MeshBasicMaterial(parameters));
});
slug_fest.core.sphere_geometry = (new THREE.SphereGeometry((30),(32),(16)));
/**
 * Create sphere geometry of radius, number of width-segments and number of height-segments.
 * Returns a THREE.SphereGeometry object.
 * see: http://threejs.org/docs/#Reference/Extras.Geometries/SphereGeometry
 */
slug_fest.core.create_sphere_geometry = (function slug_fest$core$create_sphere_geometry(radius,width_segments,height_segments){
return (new THREE.SphereGeometry(radius,width_segments,height_segments));
});
slug_fest.core.sphere_material = (new THREE.MeshLambertMaterial((function (){var obj10682 = {"color":(136)};
return obj10682;
})()));
/**
 * Create a non-shiny (Lambertian) surface of hexadecimal color. Returns a THREE.Mesh object.
 * see: http://threejs.org/docs/#Reference/Materials/MeshLambertMaterial
 */
slug_fest.core.create_mesh_lambert_material = (function slug_fest$core$create_mesh_lambert_material(color){
return (new THREE.MeshLambertMaterial((function (){var obj10686 = {"color":color};
return obj10686;
})()));
});
/**
 * Create a sphere mesh object using sphere-geometry with material with initial x,y, and z coordinates. Returns a THREE.Mesh. object
 */
slug_fest.core.create_sphere_mesh = (function slug_fest$core$create_sphere_mesh(sphere_geometry,material,x,y,z){
var sphere = (new THREE.Mesh(sphere_geometry,material));
sphere.position.set(x,y,z);

return sphere;
});
slug_fest.core.sphere = (function (){var sphere = (new THREE.Mesh(slug_fest.core.sphere_geometry,slug_fest.core.sphere_material));
sphere.position.set((0),(40),(0));

return sphere;
})();
slug_fest.core.update_controls = (function slug_fest$core$update_controls(){
slug_fest.core.controls.update();

return slug_fest.core.stats.update();
});
slug_fest.core.render = (function slug_fest$core$render(){
return slug_fest.core.renderer.render(slug_fest.core.scene,slug_fest.core.camera);
});
/**
 * Call the function callback with previous-time
 */
slug_fest.core.request_animation_frame_wrapper = (function slug_fest$core$request_animation_frame_wrapper(callback,previous_time){
var G__10690 = (function (current_time){
var G__10691 = current_time;
var G__10692 = previous_time;
return (callback.cljs$core$IFn$_invoke$arity$2 ? callback.cljs$core$IFn$_invoke$arity$2(G__10691,G__10692) : callback.call(null,G__10691,G__10692));
});
return requestAnimationFrame(G__10690);
});
/**
 * Initial loop that waits for user input in order to begin the game. This loop should be callled
 * with request-animation-frame-wrapper so that current-time and previous-time will be given proper values. current-time is provided by requstAnimationFrame
 */
slug_fest.core.initial_loop = (function slug_fest$core$initial_loop(current_time,previous_time){
var dt = (current_time - previous_time);
var chi = 0.5;
slug_fest.core.render();

slug_fest.core.update_controls();

slug_fest.core.hero_texture_current_tile_display_time = (slug_fest.core.hero_texture_current_tile_display_time + dt);

if((slug_fest.core.hero_texture_current_tile_display_time > slug_fest.core.hero_texture_tile_display_duration)){
slug_fest.core.hero_texture_current_tile = slug_fest.core.increment_texture_animation_frame_BANG_(slug_fest.core.hero_texture,(10),(1),(10),slug_fest.core.hero_texture_current_tile);
} else {
}

return slug_fest.core.request_animation_frame_wrapper(slug_fest$core$initial_loop,current_time);
});
slug_fest.core.gui = (function (){var gui = (new dat.GUI());
var parameters = (function (){var obj10694 = {"x":(0),"y":(30),"z":(0)};
return obj10694;
})();
var folder1 = gui.addFolder("Position");
var sphereX = folder1.add(parameters,"x").min((-200)).max((200)).step((1)).listen();
var sphereY = folder1.add(parameters,"y").min((0)).max((100)).step((1)).listen();
var sphereZ = folder1.add(parameters,"z").min((-200)).max((200)).step((1)).listen();
folder1.open();

sphereX.onChange(((function (gui,parameters,folder1,sphereX,sphereY,sphereZ){
return (function (value){
return slug_fest.core.light.position.x = value;
});})(gui,parameters,folder1,sphereX,sphereY,sphereZ))
);

sphereY.onChange(((function (gui,parameters,folder1,sphereX,sphereY,sphereZ){
return (function (value){
return slug_fest.core.light.position.y = value;
});})(gui,parameters,folder1,sphereX,sphereY,sphereZ))
);

sphereZ.onChange(((function (gui,parameters,folder1,sphereX,sphereY,sphereZ){
return (function (value){
return slug_fest.core.light.position.z = value;
});})(gui,parameters,folder1,sphereX,sphereY,sphereZ))
);

return gui;
})();
slug_fest.core.init = (function slug_fest$core$init(){
var light_1 = slug_fest.core.create_light((16777215),(100),(250),(100));
var light_2 = slug_fest.core.create_light((16777215),(0),(250),(100));
var floor = (function (){var floor_geometry = slug_fest.core.create_plane_geometry((1000),(1000),(10),(10));
var floor_texture = slug_fest.core.create_tiled_texture("resources/images/checkerboard.jpg",(10),(10));
var floor_material = slug_fest.core.create_mesh_basic_material((function (){var obj10706 = {"map":floor_texture,"side":THREE.DoubleSide};
return obj10706;
})());
var floor = (new THREE.Mesh(floor_geometry,floor_material));
floor.position.y = -0.5;

floor.rotation.x = (Math.PI / (2));

return floor;
})();
var skybox = (function (){var skybox_geometry = slug_fest.core.create_box_geometry((10000),(10000),(10000));
var skybox_material = slug_fest.core.create_mesh_basic_material((function (){var obj10708 = {"color":(10066329),"side":THREE.BackSide};
return obj10708;
})());
return (new THREE.Mesh(skybox_geometry,skybox_material));
})();
var runner = (function (){var runner_texture = slug_fest.core.hero_texture;
var runner_material = (new THREE.MeshBasicMaterial((function (){var obj10710 = {"map":runner_texture,"side":THREE.DoubleSide};
return obj10710;
})()));
var runner_geometry = slug_fest.core.create_plane_geometry((50),(50),(1),(1));
var runner = (new THREE.Mesh(runner_geometry,runner_material));
runner.position.set((-100),(25),(0));

return runner;
})();
slug_fest.core.scene.add(light_1);

slug_fest.core.scene.add(light_2);

slug_fest.core.scene.add(floor);

slug_fest.core.scene.add(skybox);

slug_fest.core.scene.add(runner);

var G__10711_10715 = slug_fest.core.renderer;
var G__10712_10716 = slug_fest.core.camera;
THREEx.WindowResize(G__10711_10715,G__10712_10716);

slug_fest.core.container.appendChild(slug_fest.core.stats.domElement);

THREEx.FullScreen.bindKey((function (){var obj10714 = {"charCode":"m".charCodeAt((0))};
return obj10714;
})());

return slug_fest.core.request_animation_frame_wrapper(slug_fest.core.initial_loop,(function (){return Date.now();
})());
});
goog.exportSymbol('slug_fest.core.init', slug_fest.core.init);
