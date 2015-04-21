// Compiled by ClojureScript 0.0-3126 {:optimize-constants true, :static-fns true}
goog.provide('bubble_connect.core');
goog.require('cljs.core');
bubble_connect.core.scene = (new THREE.Scene());
/**
 * Create a THREE.Scene object.
 * see:http://threejs.org/docs/#Reference/Scenes/Scene
 */
bubble_connect.core.create_scene = (function bubble_connect$core$create_scene(){
return (new THREE.Scene());
});
bubble_connect.core.camera = (function (){var camera = (new THREE.PerspectiveCamera((45),(window.innerWidth / window.innerHeight),0.1,(20000)));
bubble_connect.core.scene.add(camera);

camera.position.set((0),(150),(400));

camera.lookAt(bubble_connect.core.scene.position);

return camera;
})();
/**
 * Create a THREE.PerspectiveCamera with camera frustrum fov (field of view), aspect (aspect ratio),
 * near (near plane) and far (far plane).
 * see: http://threejs.org/docs/#Reference/Cameras/PerspectiveCamera
 */
bubble_connect.core.create_perspective_camera = (function bubble_connect$core$create_perspective_camera(fov,aspect,near,far){
return (new THREE.PerspectiveCamera(fov,aspect,near,far));
});
/**
 * Create a THREE.WebGLRenderer with js-obj parameters.
 * Example usage: (create-webgl-renderer (js-obj "antialias" true))
 * see: http://threejs.org/docs/#Reference/Renderers/WebGLRenderer
 */
bubble_connect.core.create_webgl_renderer = (function bubble_connect$core$create_webgl_renderer(parameters){
return (new THREE.WebGLRenderer(parameters));
});
bubble_connect.core.renderer = (function (){var renderer = (cljs.core.truth_(Detector.webgl)?(new THREE.WebGLRenderer((function (){var obj11116 = {"antialias":true};
return obj11116;
})())):(new THREE.CanvasRender()));
renderer.setSize(window.innerWidth,window.innerHeight);

return renderer;
})();
bubble_connect.core.container = (function (){var container = document.getElementById("ThreeJS");
container.appendChild(bubble_connect.core.renderer.domElement);

return container;
})();
bubble_connect.core.controls = (new THREE.OrbitControls(bubble_connect.core.camera,bubble_connect.core.renderer.domElement));
bubble_connect.core.stats = (function (){var stats = (new Stats());
stats.domElement.style.position = "absolute";

stats.domElement.style.bottom = "0px";

stats.domElement.style.zIndex = (100);

return stats;
})();
bubble_connect.core.light = (function (){var light = (new THREE.PointLight((16777215)));
light.position.set((100),(250),(100));

return light;
})();
/**
 * Create a light with hexadecimal color at coordinates x,y, and z
 */
bubble_connect.core.create_light = (function bubble_connect$core$create_light(color,x,y,z){
var light = (new THREE.PointLight(color));
light.position.set(x,y,z);

return light;
});
bubble_connect.core.floor_texture = (function (){var texture = (new THREE.ImageUtils.loadTexture("resources/images/checkerboard.jpg"));
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
bubble_connect.core.create_tiled_texture = (function bubble_connect$core$create_tiled_texture(image_str,U,V){
var texture = (new THREE.ImageUtils.loadTexture(image_str));
(texture["wrapS"] = THREE.RepeatWrapping);

(texture["wrapT"] = THREE.RepeatWrapping);

texture.repeat.set(U,V);

return texture;
});
bubble_connect.core.floor_material = (new THREE.MeshBasicMaterial((function (){var obj11118 = {"map":bubble_connect.core.floor_texture,"side":THREE.DoubleSide};
return obj11118;
})()));
bubble_connect.core.floor_geometry = (new THREE.PlaneGeometry((1000),(1000),(10),(10)));
/**
 * Create a THREE.PlaneGeometry using width, height, width-segments and height-segments.
 * see:http://threejs.org/docs/#Reference/Extras.Geometries/PlaneGeometry
 */
bubble_connect.core.create_plane_geometry = (function bubble_connect$core$create_plane_geometry(width,height,width_segments,height_segments){
return (new THREE.PlaneGeometry(width,height,width_segments,height_segments));
});
/**
 * Create a box geometry that of width, height and depth.
 * see: http://threejs.org/docs/#Reference/Extras.Geometries/BoxGeometry
 */
bubble_connect.core.create_box_geometry = (function bubble_connect$core$create_box_geometry(width,height,depth){
return (new THREE.BoxGeometry(width,height,depth));
});
bubble_connect.core.skybox_geometry = (new THREE.CubeGeometry((10000),(10000),(10000)));
bubble_connect.core.skybox_material = (new THREE.MeshBasicMaterial((function (){var obj11120 = {"color":(10066431),"side":THREE.BackSide};
return obj11120;
})()));
/**
 * Create a THREE.MeshBasicMaterial object using the js-obj parameters.
 * Example usage: (create-mesh-basic-material (js-obj "color" 0x9999ff "side" js/THREE.BackSide))
 * see: http://threejs.org/docs/#Reference/Materials/MeshBasicMaterial
 */
bubble_connect.core.create_mesh_basic_material = (function bubble_connect$core$create_mesh_basic_material(parameters){
return (new THREE.MeshBasicMaterial(parameters));
});
bubble_connect.core.sphere_geometry = (new THREE.SphereGeometry((30),(32),(16)));
/**
 * Create sphere geometry of radius, number of width-segments and number of height-segments.
 * Returns a THREE.SphereGeometry object.
 * see: http://threejs.org/docs/#Reference/Extras.Geometries/SphereGeometry
 */
bubble_connect.core.create_sphere_geometry = (function bubble_connect$core$create_sphere_geometry(radius,width_segments,height_segments){
return (new THREE.SphereGeometry(radius,width_segments,height_segments));
});
bubble_connect.core.sphere_material = (new THREE.MeshLambertMaterial((function (){var obj11122 = {"color":(136)};
return obj11122;
})()));
/**
 * Create a non-shiny (Lambertian) surface of hexadecimal color. Returns a THREE.Mesh object.
 * see: http://threejs.org/docs/#Reference/Materials/MeshLambertMaterial
 */
bubble_connect.core.create_mesh_lambert_material = (function bubble_connect$core$create_mesh_lambert_material(color){
return (new THREE.MeshLambertMaterial((function (){var obj11126 = {"color":color};
return obj11126;
})()));
});
/**
 * Create a sphere mesh object using sphere-geometry with material with initial x,y, and z coordinates. Returns a THREE.Mesh. object
 */
bubble_connect.core.create_sphere_mesh = (function bubble_connect$core$create_sphere_mesh(sphere_geometry,material,x,y,z){
var sphere = (new THREE.Mesh(sphere_geometry,material));
sphere.position.set(x,y,z);

return sphere;
});
bubble_connect.core.sphere = (function (){var sphere = (new THREE.Mesh(bubble_connect.core.sphere_geometry,bubble_connect.core.sphere_material));
sphere.position.set((0),(40),(0));

return sphere;
})();
bubble_connect.core.update_controls = (function bubble_connect$core$update_controls(){
bubble_connect.core.controls.update();

return bubble_connect.core.stats.update();
});
bubble_connect.core.render = (function bubble_connect$core$render(){
return bubble_connect.core.renderer.render(bubble_connect.core.scene,bubble_connect.core.camera);
});
bubble_connect.core.animate = (function bubble_connect$core$animate(){
var G__11128_11129 = bubble_connect$core$animate;
requestAnimationFrame(G__11128_11129);

bubble_connect.core.render();

return bubble_connect.core.update_controls();
});
bubble_connect.core.gui = (function (){var gui = (new dat.GUI());
var parameters = (function (){var obj11131 = {"x":(0),"y":(30),"z":(0)};
return obj11131;
})();
var folder1 = gui.addFolder("Position");
var sphereX = folder1.add(parameters,"x").min((-200)).max((200)).step((1)).listen();
var sphereY = folder1.add(parameters,"y").min((0)).max((100)).step((1)).listen();
var sphereZ = folder1.add(parameters,"z").min((-200)).max((200)).step((1)).listen();
folder1.open();

sphereX.onChange(((function (gui,parameters,folder1,sphereX,sphereY,sphereZ){
return (function (value){
return bubble_connect.core.light.position.x = value;
});})(gui,parameters,folder1,sphereX,sphereY,sphereZ))
);

sphereY.onChange(((function (gui,parameters,folder1,sphereX,sphereY,sphereZ){
return (function (value){
return bubble_connect.core.light.position.y = value;
});})(gui,parameters,folder1,sphereX,sphereY,sphereZ))
);

sphereZ.onChange(((function (gui,parameters,folder1,sphereX,sphereY,sphereZ){
return (function (value){
return bubble_connect.core.light.position.z = value;
});})(gui,parameters,folder1,sphereX,sphereY,sphereZ))
);

return gui;
})();
bubble_connect.core.init = (function bubble_connect$core$init(){
var light_1 = bubble_connect.core.create_light((16777215),(100),(250),(100));
var light_2 = bubble_connect.core.create_light((16777215),(0),(250),(100));
var floor = (function (){var floor_geometry = bubble_connect.core.create_plane_geometry((1000),(1000),(10),(10));
var floor_texture = bubble_connect.core.create_tiled_texture("resources/images/checkerboard.jpg",(10),(10));
var floor_material = bubble_connect.core.create_mesh_basic_material((function (){var obj11141 = {"map":floor_texture,"side":THREE.DoubleSide};
return obj11141;
})());
var floor = (new THREE.Mesh(floor_geometry,floor_material));
floor.position.y = -0.5;

floor.rotation.x = (Math.PI / (2));

return floor;
})();
var skybox = (function (){var skybox_geometry = bubble_connect.core.create_box_geometry((10000),(10000),(10000));
var skybox_material = bubble_connect.core.create_mesh_basic_material((function (){var obj11143 = {"color":(10066329),"side":THREE.BackSide};
return obj11143;
})());
return (new THREE.Mesh(skybox_geometry,skybox_material));
})();
bubble_connect.core.scene.add(light_1);

bubble_connect.core.scene.add(light_2);

bubble_connect.core.scene.add(floor);

bubble_connect.core.scene.add(skybox);

bubble_connect.core.scene.add(bubble_connect.core.create_sphere_mesh(bubble_connect.core.create_sphere_geometry((30),(32),(16)),bubble_connect.core.create_mesh_lambert_material((136)),(0),(60),(0)));

bubble_connect.core.scene.add(bubble_connect.core.create_sphere_mesh(bubble_connect.core.create_sphere_geometry((30),(32),(16)),bubble_connect.core.create_mesh_lambert_material((8912896)),(60),(40),(0)));

bubble_connect.core.scene.add(bubble_connect.core.create_sphere_mesh(bubble_connect.core.create_sphere_geometry((30),(32),(16)),bubble_connect.core.create_mesh_lambert_material((34816)),(0),(40),(60)));

var G__11144_11148 = bubble_connect.core.renderer;
var G__11145_11149 = bubble_connect.core.camera;
THREEx.WindowResize(G__11144_11148,G__11145_11149);

bubble_connect.core.container.appendChild(bubble_connect.core.stats.domElement);

THREEx.FullScreen.bindKey((function (){var obj11147 = {"charCode":"m".charCodeAt((0))};
return obj11147;
})());

return bubble_connect.core.animate();
});
goog.exportSymbol('bubble_connect.core.init', bubble_connect.core.init);
