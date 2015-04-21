// Compiled by ClojureScript 0.0-3126 {}
goog.provide('slug_fest.core');
goog.require('cljs.core');
goog.require('dat');
goog.require('stats');
goog.require('detector');
goog.require('window_resize');
goog.require('fullscreen');
goog.require('three');
slug_fest.core.initial_time = cljs.core.atom.call(null,null);
/**
 * Return the x-max that will keep on object on ground given the object and ground
 */
slug_fest.core.x_max = (function slug_fest$core$x_max(object,ground){
return ((ground.geometry.parameters.width / (2)) - (object.geometry.parameters.width / (2)));
});
/**
 * Return the x-min that will keep on object on ground given the object and ground
 */
slug_fest.core.x_min = (function slug_fest$core$x_min(object,ground){
return ((object.geometry.parameters.width / (2)) - (ground.geometry.parameters.width / (2)));
});
/**
 * Return the y-max that will keep on object on ground given the geometries of object and ground
 */
slug_fest.core.y_max = (function slug_fest$core$y_max(object,ground){
return ((ground.geometry.parameters.height / (2)) - (object.geometry.parameters.height / (2)));
});
/**
 * Return the y-min that will keep on object on ground given the object and ground
 */
slug_fest.core.y_min = (function slug_fest$core$y_min(object,ground){
return ((object.geometry.parameters.height / (2)) - (ground.geometry.parameters.height / (2)));
});
/**
 * Return a box that is the same size of an objects's mesh and centered on that object
 */
slug_fest.core.mesh_box = (function slug_fest$core$mesh_box(object){
var box = (new THREE.Box2((new THREE.Vector2((0),(1))),(new THREE.Vector2((1),(0)))));
box.setFromCenterAndSize((new THREE.Vector2(object.mesh.position.x,object.mesh.position.y)),(new THREE.Vector2(object.mesh.geometry.parameters.width,object.mesh.geometry.parameters.height)));

return box;
});
/**
 * Find the closest object to target in object-pool
 */
slug_fest.core.find_nearest_object = (function slug_fest$core$find_nearest_object(target,object_pool,nearest_object){
if(cljs.core.empty_QMARK_.call(null,object_pool)){
return nearest_object;
} else {
if((nearest_object == null)){
return slug_fest$core$find_nearest_object.call(null,target,cljs.core.rest.call(null,object_pool),cljs.core.first.call(null,object_pool));
} else {
var nearest_distance = target.mesh.position.distanceTo(nearest_object.mesh.position);
var next_object = cljs.core.first.call(null,object_pool);
var next_distance = target.mesh.position.distanceTo(next_object.mesh.position);
if((nearest_distance < next_distance)){
return slug_fest$core$find_nearest_object.call(null,target,cljs.core.rest.call(null,object_pool),nearest_object);
} else {
return slug_fest$core$find_nearest_object.call(null,target,cljs.core.rest.call(null,object_pool),next_object);
}

}
}
});

/**
* @constructor
*/
slug_fest.core.Text = (function (texture,geometry,material,mesh){
this.texture = texture;
this.geometry = geometry;
this.material = material;
this.mesh = mesh;
})

slug_fest.core.Text.cljs$lang$type = true;

slug_fest.core.Text.cljs$lang$ctorStr = "slug-fest.core/Text";

slug_fest.core.Text.cljs$lang$ctorPrWriter = (function (this__4645__auto__,writer__4646__auto__,opt__4647__auto__){
return cljs.core._write.call(null,writer__4646__auto__,"slug-fest.core/Text");
});

slug_fest.core.__GT_Text = (function slug_fest$core$__GT_Text(texture,geometry,material,mesh){
return (new slug_fest.core.Text(texture,geometry,material,mesh));
});

slug_fest.core.game_over_text = (function (){var texture = (new THREE.ImageUtils.loadTexture("resources/images/gameover.gif"));
var geometry = (new THREE.PlaneGeometry((1000),(219),(1),(1)));
var material = (new THREE.MeshBasicMaterial((function (){var obj21206 = {"map":texture,"side":THREE.DoubleSide,"transparent":true};
return obj21206;
})()));
var mesh = (new THREE.Mesh(geometry,material));
mesh.position.z = (10);

material.opacity = (0);

return (new slug_fest.core.Text(texture,geometry,material,mesh));
})();
slug_fest.core.you_win_text = (function (){var texture = (new THREE.ImageUtils.loadTexture("resources/images/youwin.gif"));
var geometry = (new THREE.PlaneGeometry((1000),(219),(1),(1)));
var material = (new THREE.MeshBasicMaterial((function (){var obj21208 = {"map":texture,"side":THREE.DoubleSide,"transparent":true};
return obj21208;
})()));
var mesh = (new THREE.Mesh(geometry,material));
mesh.position.z = (10);

material.opacity = (0);

return (new slug_fest.core.Text(texture,geometry,material,mesh));
})();

/**
* @constructor
*/
slug_fest.core.Ground = (function (texture,geometry,material,mesh){
this.texture = texture;
this.geometry = geometry;
this.material = material;
this.mesh = mesh;
})

slug_fest.core.Ground.cljs$lang$type = true;

slug_fest.core.Ground.cljs$lang$ctorStr = "slug-fest.core/Ground";

slug_fest.core.Ground.cljs$lang$ctorPrWriter = (function (this__4645__auto__,writer__4646__auto__,opt__4647__auto__){
return cljs.core._write.call(null,writer__4646__auto__,"slug-fest.core/Ground");
});

slug_fest.core.__GT_Ground = (function slug_fest$core$__GT_Ground(texture,geometry,material,mesh){
return (new slug_fest.core.Ground(texture,geometry,material,mesh));
});

slug_fest.core.ground = (function (){var texture = (new THREE.ImageUtils.loadTexture("resources/images/ground.png"));
var geometry = (new THREE.PlaneGeometry((1000),(1000),(10),(10)));
var material = (new THREE.MeshBasicMaterial((function (){var obj21210 = {"map":texture,"side":THREE.DoubleSide};
return obj21210;
})()));
var mesh = (new THREE.Mesh(geometry,material));
(texture["wrapS"] = THREE.RepeatWrapping);

(texture["wrapT"] = THREE.RepeatWrapping);

texture.repeat.set((10),(10));

return (new slug_fest.core.Ground(texture,geometry,material,mesh));
})();

/**
* @constructor
*/
slug_fest.core.Hero = (function (texture,horizontal_frames,vertical_frames,material,geometry,mesh,current_frame,current_frame_total_display_time,frame_duration,direction,move_increment,salting_QMARK_,current_salt_frame,salt_frame_display_time,salt_frame_duration,shaker_mesh,shaker_box){
this.texture = texture;
this.horizontal_frames = horizontal_frames;
this.vertical_frames = vertical_frames;
this.material = material;
this.geometry = geometry;
this.mesh = mesh;
this.current_frame = current_frame;
this.current_frame_total_display_time = current_frame_total_display_time;
this.frame_duration = frame_duration;
this.direction = direction;
this.move_increment = move_increment;
this.salting_QMARK_ = salting_QMARK_;
this.current_salt_frame = current_salt_frame;
this.salt_frame_display_time = salt_frame_display_time;
this.salt_frame_duration = salt_frame_duration;
this.shaker_mesh = shaker_mesh;
this.shaker_box = shaker_box;
})
slug_fest.core.Hero.prototype.shaker_box_intersects_slug_QMARK_ = (function (slug){
var self__ = this;
var this$ = this;
return self__.shaker_box.isIntersectionBox(slug_fest.core.mesh_box.call(null,slug));
});

slug_fest.core.Hero.prototype.increment_animation_frame = (function (){
var self__ = this;
var this$ = this;
if((self__.current_frame_total_display_time > self__.frame_duration)){
var current_frame__$1 = ((cljs.core._EQ_.call(null,self__.current_frame,(self__.horizontal_frames * self__.vertical_frames)))?(0):self__.current_frame);
var current_column = cljs.core.mod.call(null,current_frame__$1,self__.horizontal_frames);
var current_row = (1);
self__.texture.offset.x = (current_column / self__.horizontal_frames);

self__.texture.offset.y = (current_row / self__.vertical_frames);

this$.current_frame = (current_frame__$1 + (1));

return this$.current_frame_total_display_time = (0);
} else {
return null;
}
});

slug_fest.core.Hero.prototype.salt = (function (slug,dt){
var self__ = this;
var this$ = this;
this$.salt_frame_display_time = (self__.salt_frame_display_time + dt);

if(cljs.core.truth_(this$.shaker_box_intersects_slug_QMARK_(slug))){
slug.increase_salt_BANG_(dt);
} else {
}

if((self__.salt_frame_display_time > self__.salt_frame_duration)){
var salt_frames_width = (7);
var salt_frames = (7);
var current_frame__$1 = ((cljs.core._EQ_.call(null,self__.current_salt_frame,salt_frames))?(0):self__.current_salt_frame);
var current_coloumn = cljs.core.mod.call(null,current_frame__$1,salt_frames);
var current_row = (0);
this$.salting_QMARK_ = true;

this$.texture.offset.x = (current_coloumn / salt_frames_width);

this$.texture.offset.y = (current_row / (1));

this$.current_salt_frame = (current_frame__$1 + (1));

return this$.salt_frame_display_time = (0);
} else {
return null;
}
});

slug_fest.core.Hero.prototype.move_left = (function (ground){
var self__ = this;
var this$ = this;
var new_position = (self__.mesh.position.x - self__.move_increment);
var padding = (-70);
var x_min = (slug_fest.core.x_min.call(null,this$,ground) + padding);
if((new_position < x_min)){
self__.mesh.position.x = x_min;
} else {
self__.mesh.translateX(self__.move_increment);
}

if(cljs.core._EQ_.call(null,self__.direction,"right")){
self__.mesh.rotateY(Math.PI);

return this$.direction = "left";
} else {
return null;
}
});

slug_fest.core.Hero.prototype.update_shaker_box = (function (){
var self__ = this;
var this$ = this;
var x_offset = (65);
var y_offset = (50);
var width = (45);
var height = (90);
if(cljs.core._EQ_.call(null,self__.direction,"right")){
self__.shaker_box.setFromCenterAndSize((new THREE.Vector2((this$.mesh.position.x + x_offset),(this$.mesh.position.y - y_offset))),(new THREE.Vector2(width,height)));

return self__.shaker_mesh.position.set((this$.mesh.position.x + x_offset),(this$.mesh.position.y - y_offset),this$.mesh.position.z);
} else {
self__.shaker_box.setFromCenterAndSize((new THREE.Vector2((this$.mesh.position.x - x_offset),(this$.mesh.position.y - y_offset))),(new THREE.Vector2(width,height)));

return self__.shaker_mesh.position.set((this$.mesh.position.x - x_offset),(this$.mesh.position.y - y_offset),this$.mesh.position.z);
}
});

slug_fest.core.Hero.prototype.increment_frame_display_time = (function (dt){
var self__ = this;
var this$ = this;
return this$.current_frame_total_display_time = (self__.current_frame_total_display_time + dt);
});

slug_fest.core.Hero.prototype.move_down = (function (ground){
var self__ = this;
var this$ = this;
var new_position = (self__.mesh.position.y - self__.move_increment);
var y_min = slug_fest.core.y_min.call(null,this$,ground);
if((new_position < y_min)){
return self__.mesh.position.y = y_min;
} else {
return self__.mesh.translateY(((-1) * self__.move_increment));
}
});

slug_fest.core.Hero.prototype.stop_salting = (function (){
var self__ = this;
var this$ = this;
this$.salting_QMARK_ = false;

this$.current_salt_frame = (0);

this$.salt_frame_display_time = (0);

this$.texture.offset.x = (0);

return this$.texture.offset.y = ((1) / (2));
});

slug_fest.core.Hero.prototype.move_right = (function (ground){
var self__ = this;
var this$ = this;
var new_position = (self__.mesh.position.x + self__.move_increment);
var padding = (70);
var x_max = (slug_fest.core.x_max.call(null,this$,ground) + padding);
if((new_position > x_max)){
self__.mesh.position.x = x_max;
} else {
self__.mesh.translateX(self__.move_increment);
}

if(cljs.core._EQ_.call(null,self__.direction,"left")){
self__.mesh.rotateY(Math.PI);

return this$.direction = "right";
} else {
return null;
}
});

slug_fest.core.Hero.prototype.move_up = (function (ground){
var self__ = this;
var this$ = this;
var new_position = (self__.mesh.position.y + self__.move_increment);
var padding = (150);
var y_max = (slug_fest.core.y_max.call(null,this$,ground) + padding);
if((new_position > y_max)){
return self__.mesh.position.y = y_max;
} else {
return self__.mesh.translateY(self__.move_increment);
}
});

slug_fest.core.Hero.cljs$lang$type = true;

slug_fest.core.Hero.cljs$lang$ctorStr = "slug-fest.core/Hero";

slug_fest.core.Hero.cljs$lang$ctorPrWriter = (function (this__4645__auto__,writer__4646__auto__,opt__4647__auto__){
return cljs.core._write.call(null,writer__4646__auto__,"slug-fest.core/Hero");
});

slug_fest.core.__GT_Hero = (function slug_fest$core$__GT_Hero(texture,horizontal_frames,vertical_frames,material,geometry,mesh,current_frame,current_frame_total_display_time,frame_duration,direction,move_increment,salting_QMARK_,current_salt_frame,salt_frame_display_time,salt_frame_duration,shaker_mesh,shaker_box){
return (new slug_fest.core.Hero(texture,horizontal_frames,vertical_frames,material,geometry,mesh,current_frame,current_frame_total_display_time,frame_duration,direction,move_increment,salting_QMARK_,current_salt_frame,salt_frame_display_time,salt_frame_duration,shaker_mesh,shaker_box));
});

slug_fest.core.hero = (function (){var texture = (new THREE.ImageUtils.loadTexture("resources/images/gnome.gif"));
var horizontal_frames = (1);
var vertical_frames = (2);
var material = (new THREE.MeshBasicMaterial((function (){var obj21212 = {"map":texture,"side":THREE.DoubleSide,"transparent":true};
return obj21212;
})()));
var geometry = (new THREE.PlaneGeometry((200),(200),(1),(1)));
var mesh = (new THREE.Mesh(geometry,material));
var start_frame = (0);
var current_frame_total_display_time = (0);
var frame_duration = (75);
var direction = "right";
var move_increment = (5);
var salting_QMARK_ = false;
var current_salt_frame = (0);
var salt_frame_display_time = (0);
var salt_frame_duration = (75);
var shaker_geometry = (new THREE.BoxGeometry((45),(90),(1)));
var shaker_material = (new THREE.MeshBasicMaterial((16711680)));
var shaker_mesh = (new THREE.Mesh(shaker_geometry,shaker_material));
var shaker_box = (new THREE.Box2((new THREE.Vector2((0),(1))),(new THREE.Vector2((1),(0)))));
(texture["wrapS"] = THREE.RepeatWrapping);

(texture["wrapT"] = THREE.RepeatWrapping);

texture.repeat.set(((1) / (7)),((1) / (2)));

mesh.position.set((0),(0),(1));

shaker_mesh.position.set((0),(0),(1));

return (new slug_fest.core.Hero(texture,horizontal_frames,vertical_frames,material,geometry,mesh,start_frame,current_frame_total_display_time,frame_duration,direction,move_increment,salting_QMARK_,current_salt_frame,salt_frame_display_time,salt_frame_duration,shaker_mesh,shaker_box));
})();

/**
* @constructor
*/
slug_fest.core.Shroom = (function (texture,geometry,material,mesh,bite_time,max_bite_time,dead_QMARK_){
this.texture = texture;
this.geometry = geometry;
this.material = material;
this.mesh = mesh;
this.bite_time = bite_time;
this.max_bite_time = max_bite_time;
this.dead_QMARK_ = dead_QMARK_;
})
slug_fest.core.Shroom.prototype.increase_bites_BANG_ = (function (dt){
var self__ = this;
var this$ = this;
this$.bite_time = (self__.bite_time + dt);

self__.material.opacity = (((1) - (self__.bite_time / self__.max_bite_time)) + 0.5);

if((self__.bite_time >= self__.max_bite_time)){
self__.mesh.position.x = (1000);

self__.material.opacity = (0);

return this$.dead_QMARK_ = true;
} else {
return null;
}
});

slug_fest.core.Shroom.cljs$lang$type = true;

slug_fest.core.Shroom.cljs$lang$ctorStr = "slug-fest.core/Shroom";

slug_fest.core.Shroom.cljs$lang$ctorPrWriter = (function (this__4645__auto__,writer__4646__auto__,opt__4647__auto__){
return cljs.core._write.call(null,writer__4646__auto__,"slug-fest.core/Shroom");
});

slug_fest.core.__GT_Shroom = (function slug_fest$core$__GT_Shroom(texture,geometry,material,mesh,bite_time,max_bite_time,dead_QMARK_){
return (new slug_fest.core.Shroom(texture,geometry,material,mesh,bite_time,max_bite_time,dead_QMARK_));
});

slug_fest.core.shroom = (function slug_fest$core$shroom(){
var texture = (new THREE.ImageUtils.loadTexture("resources/images/mushroom.png"));
var geometry = (new THREE.PlaneGeometry((100),(100),(1),(1)));
var material = (new THREE.MeshBasicMaterial((function (){var obj21216 = {"map":texture,"side":THREE.DoubleSide,"transparent":true};
return obj21216;
})()));
var mesh = (new THREE.Mesh(geometry,material));
var bite_time = (0);
var max_bite_time = (2000);
var dead_QMARK_ = false;
return (new slug_fest.core.Shroom(texture,geometry,material,mesh,bite_time,max_bite_time,dead_QMARK_));
});
/**
 * Reset the shroom pool
 */
slug_fest.core.reset_shroom_pool_BANG_ = (function slug_fest$core$reset_shroom_pool_BANG_(shroom_pool){
var first_shroom = cljs.core.nth.call(null,shroom_pool,(0));
var second_shroom = cljs.core.nth.call(null,shroom_pool,(1));
var third_shroom = cljs.core.nth.call(null,shroom_pool,(2));
var fourth_shroom = cljs.core.nth.call(null,shroom_pool,(3));
var fifth_shroom = cljs.core.nth.call(null,shroom_pool,(4));
first_shroom.mesh.position.x = (slug_fest.core.x_min.call(null,first_shroom,slug_fest.core.ground) + (300));

first_shroom.mesh.position.y = (slug_fest.core.y_max.call(null,first_shroom,slug_fest.core.ground) - (300));

second_shroom.mesh.position.x = (slug_fest.core.x_max.call(null,second_shroom,slug_fest.core.ground) - (300));

second_shroom.mesh.position.y = (slug_fest.core.y_max.call(null,second_shroom,slug_fest.core.ground) - (300));

third_shroom.mesh.position.x = (slug_fest.core.x_min.call(null,third_shroom,slug_fest.core.ground) + (300));

third_shroom.mesh.position.y = (slug_fest.core.y_min.call(null,third_shroom,slug_fest.core.ground) + (300));

fourth_shroom.mesh.position.x = (slug_fest.core.x_max.call(null,fourth_shroom,slug_fest.core.ground) - (300));

fourth_shroom.mesh.position.y = (slug_fest.core.y_min.call(null,fourth_shroom,slug_fest.core.ground) + (300));

fifth_shroom.mesh.position.x = (0);

fifth_shroom.mesh.position.y = (0);

cljs.core.doall.call(null,cljs.core.map.call(null,((function (first_shroom,second_shroom,third_shroom,fourth_shroom,fifth_shroom){
return (function (shroom){
shroom.material.opacity = (1);

shroom.dead_QMARK_ = false;

return shroom.bite_time = (0);
});})(first_shroom,second_shroom,third_shroom,fourth_shroom,fifth_shroom))
,shroom_pool));

return shroom_pool;
});
/**
 * Create a shroom pool
 */
slug_fest.core.create_shroom_pool = (function slug_fest$core$create_shroom_pool(){
var pool = cljs.core.repeatedly.call(null,(5),(function (){
return slug_fest.core.shroom.call(null);
}));
return slug_fest.core.reset_shroom_pool_BANG_.call(null,pool);
});

/**
* @constructor
*/
slug_fest.core.Slug = (function (texture,geometry,material,mesh,salt_time,max_salt_time,dead_QMARK_){
this.texture = texture;
this.geometry = geometry;
this.material = material;
this.mesh = mesh;
this.salt_time = salt_time;
this.max_salt_time = max_salt_time;
this.dead_QMARK_ = dead_QMARK_;
})
slug_fest.core.Slug.prototype.increase_salt_BANG_ = (function (dt){
var self__ = this;
var this$ = this;
this$.salt_time = (self__.salt_time + dt);

self__.material.opacity = (((1) - (self__.salt_time / self__.max_salt_time)) + 0.5);

if((self__.salt_time >= self__.max_salt_time)){
self__.mesh.position.x = (1000);

self__.material.opacity = (0);

return this$.dead_QMARK_ = true;
} else {
return null;
}
});

slug_fest.core.Slug.prototype.find_nearest_shroom = (function (shroom_pool){
var self__ = this;
var this$ = this;
return slug_fest.core.find_nearest_object.call(null,this$,shroom_pool);
});

slug_fest.core.Slug.prototype.seek_nearest_shroom = (function (shroom_pool){
var self__ = this;
var this$ = this;
var nearest_shroom = this$.find_nearest_shroom(shroom_pool);
var slug_x = this$.mesh.position.x;
var shroom_x = nearest_shroom.mesh.position.x;
var slug_y = this$.mesh.position.y;
var shroom_y = nearest_shroom.mesh.position.y;
var change_coord = ((function (nearest_shroom,slug_x,shroom_x,slug_y,shroom_y,this$){
return (function (c1,c2,dc){
var abs = ((function (nearest_shroom,slug_x,shroom_x,slug_y,shroom_y,this$){
return (function (n){
if((n > (0))){
return n;
} else {
return ((-1) * n);
}
});})(nearest_shroom,slug_x,shroom_x,slug_y,shroom_y,this$))
;
if((abs.call(null,(c1 - c2)) <= dc)){
return c2;
} else {
if((c1 > c2)){
return (c1 - dc);
} else {
if((c1 < c2)){
return (c1 + dc);
} else {
return null;
}
}
}
});})(nearest_shroom,slug_x,shroom_x,slug_y,shroom_y,this$))
;
var dc = 1.4;
this$.mesh.position.x = change_coord.call(null,slug_x,shroom_x,dc);

return this$.mesh.position.y = change_coord.call(null,slug_y,shroom_y,dc);
});

slug_fest.core.Slug.prototype.eat = (function (shroom_pool,dt){
var self__ = this;
var this$ = this;
var slug_box = slug_fest.core.mesh_box.call(null,this$);
var nearest_shroom = this$.find_nearest_shroom(shroom_pool);
var shroom_box = slug_fest.core.mesh_box.call(null,nearest_shroom);
if(cljs.core.truth_(slug_box.isIntersectionBox(shroom_box))){
return nearest_shroom.increase_bites_BANG_(dt);
} else {
return null;
}
});

slug_fest.core.Slug.cljs$lang$type = true;

slug_fest.core.Slug.cljs$lang$ctorStr = "slug-fest.core/Slug";

slug_fest.core.Slug.cljs$lang$ctorPrWriter = (function (this__4645__auto__,writer__4646__auto__,opt__4647__auto__){
return cljs.core._write.call(null,writer__4646__auto__,"slug-fest.core/Slug");
});

slug_fest.core.__GT_Slug = (function slug_fest$core$__GT_Slug(texture,geometry,material,mesh,salt_time,max_salt_time,dead_QMARK_){
return (new slug_fest.core.Slug(texture,geometry,material,mesh,salt_time,max_salt_time,dead_QMARK_));
});

slug_fest.core.slug = (function slug_fest$core$slug(){
var texture = (new THREE.ImageUtils.loadTexture("resources/images/slug.png"));
var horizontal_frames = (1);
var vertical_frames = (1);
var material = (new THREE.MeshBasicMaterial((function (){var obj21220 = {"map":texture,"side":THREE.DoubleSide,"transparent":true};
return obj21220;
})()));
var geometry = (new THREE.PlaneGeometry((150),(70),(1),(1)));
var mesh = (new THREE.Mesh(geometry,material));
var salt_time = (0);
var max_salt_time = (1000);
var dead_QMARK_ = false;
(texture["wrapS"] = THREE.RepeatWrapping);

(texture["wrapT"] = THREE.RepeatWrapping);

texture.repeat.set(((1) / (10)),((1) / (1)));

texture.offset.x = (0);

texture.offset.y = (0);

return (new slug_fest.core.Slug(texture,geometry,material,mesh,salt_time,max_salt_time,dead_QMARK_));
});
/**
 * Reset the slug pool
 */
slug_fest.core.reset_slug_pool_BANG_ = (function slug_fest$core$reset_slug_pool_BANG_(slug_pool){
var first_slug = cljs.core.nth.call(null,slug_pool,(0));
var second_slug = cljs.core.nth.call(null,slug_pool,(1));
var third_slug = cljs.core.nth.call(null,slug_pool,(2));
var fourth_slug = cljs.core.nth.call(null,slug_pool,(3));
var fifth_slug = cljs.core.nth.call(null,slug_pool,(4));
first_slug.mesh.position.x = slug_fest.core.x_min.call(null,first_slug,slug_fest.core.ground);

first_slug.mesh.position.y = slug_fest.core.y_max.call(null,first_slug,slug_fest.core.ground);

second_slug.mesh.position.x = slug_fest.core.x_max.call(null,second_slug,slug_fest.core.ground);

second_slug.mesh.position.y = slug_fest.core.y_max.call(null,second_slug,slug_fest.core.ground);

third_slug.mesh.position.x = slug_fest.core.x_min.call(null,third_slug,slug_fest.core.ground);

third_slug.mesh.position.y = slug_fest.core.y_min.call(null,third_slug,slug_fest.core.ground);

fourth_slug.mesh.position.x = slug_fest.core.x_max.call(null,fourth_slug,slug_fest.core.ground);

fourth_slug.mesh.position.y = slug_fest.core.y_min.call(null,fourth_slug,slug_fest.core.ground);

fifth_slug.mesh.position.x = (slug_fest.core.x_max.call(null,fifth_slug,slug_fest.core.ground) + (100));

fifth_slug.mesh.position.y = (slug_fest.core.y_min.call(null,fifth_slug,slug_fest.core.ground) + (100));

cljs.core.doall.call(null,cljs.core.map.call(null,((function (first_slug,second_slug,third_slug,fourth_slug,fifth_slug){
return (function (slug){
slug.dead_QMARK_ = false;

slug.salt_time = (0);

return slug.material.opacity = (1);
});})(first_slug,second_slug,third_slug,fourth_slug,fifth_slug))
,slug_pool));

return slug_pool;
});
/**
 * Create a slug pool
 */
slug_fest.core.create_slug_pool = (function slug_fest$core$create_slug_pool(){
var pool = cljs.core.repeatedly.call(null,(5),(function (){
return slug_fest.core.slug.call(null);
}));
var first_slug = cljs.core.nth.call(null,pool,(0));
var second_slug = cljs.core.nth.call(null,pool,(1));
var third_slug = cljs.core.nth.call(null,pool,(2));
var fourth_slug = cljs.core.nth.call(null,pool,(3));
var fifth_slug = cljs.core.nth.call(null,pool,(4));
return slug_fest.core.reset_slug_pool_BANG_.call(null,pool);
});
slug_fest.core.key_state = (function (){var obj21222 = {};
return obj21222;
})();
slug_fest.core.left_arrow = (37);
slug_fest.core.up_arrow = (38);
slug_fest.core.right_arrow = (39);
slug_fest.core.down_arrow = (40);
slug_fest.core.a_key = (65);
slug_fest.core.w_key = (87);
slug_fest.core.d_key = (68);
slug_fest.core.s_key = (83);
slug_fest.core.space_key = (32);
/**
 * Handle event related to when a user presses down on a key. This modifies key-state
 */
slug_fest.core.game_key_down_BANG_ = (function slug_fest$core$game_key_down_BANG_(event){
return (slug_fest.core.key_state[(function (){var or__4066__auto__ = event.keycode;
if(cljs.core.truth_(or__4066__auto__)){
return or__4066__auto__;
} else {
return event.which;
}
})()] = true);
});
/**
 * Handle event related to when a user releases a key. This modifies key-state
 */
slug_fest.core.game_key_up_BANG_ = (function slug_fest$core$game_key_up_BANG_(event){
return (slug_fest.core.key_state[(function (){var or__4066__auto__ = event.keycode;
if(cljs.core.truth_(or__4066__auto__)){
return or__4066__auto__;
} else {
return event.which;
}
})()] = false);
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

camera.position.set((0),(0),(1300));

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
slug_fest.core.renderer = (function (){var renderer = (cljs.core.truth_(Detector.webgl)?(new THREE.WebGLRenderer((function (){var obj21224 = {"antialias":true};
return obj21224;
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
slug_fest.core.floor_material = (new THREE.MeshBasicMaterial((function (){var obj21226 = {"map":slug_fest.core.floor_texture,"side":THREE.DoubleSide};
return obj21226;
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
slug_fest.core.skybox_material = (new THREE.MeshBasicMaterial((function (){var obj21228 = {"color":(8081953),"side":THREE.BackSide};
return obj21228;
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
slug_fest.core.sphere_material = (new THREE.MeshLambertMaterial((function (){var obj21230 = {"color":(136)};
return obj21230;
})()));
/**
 * Create a non-shiny (Lambertian) surface of hexadecimal color. Returns a THREE.Mesh object.
 * see: http://threejs.org/docs/#Reference/Materials/MeshLambertMaterial
 */
slug_fest.core.create_mesh_lambert_material = (function slug_fest$core$create_mesh_lambert_material(color){
return (new THREE.MeshLambertMaterial((function (){var obj21234 = {"color":color};
return obj21234;
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
return requestAnimationFrame((function (current_time){
return callback.call(null,current_time,previous_time);
}));
});
slug_fest.core.delay = (0);
/**
 * Game is over
 */
slug_fest.core.game_over = (function slug_fest$core$game_over(current_time,previous_time){
var previous_time__$1 = ((cljs.core._EQ_.call(null,previous_time,null))?current_time:previous_time);
var dt = (current_time - previous_time__$1);
var max_delay = (1000);
slug_fest.core.delay = (slug_fest.core.delay + dt);

slug_fest.core.render.call(null);

if((slug_fest.core.delay > max_delay)){
if(cljs.core.truth_((slug_fest.core.key_state[slug_fest.core.space_key]))){
slug_fest.core.slug_pool = slug_fest.core.reset_slug_pool_BANG_.call(null,slug_fest.core.slug_pool);

slug_fest.core.shroom_pool = slug_fest.core.reset_shroom_pool_BANG_.call(null,slug_fest.core.shroom_pool);

slug_fest.core.delay = (0);

slug_fest.core.hero.mesh.position.x = (0);

slug_fest.core.hero.mesh.position.y = (150);

cancelAnimationFrame(slug_fest.core.request_id);

return slug_fest.core.request_id = slug_fest.core.request_animation_frame_wrapper.call(null,slug_fest.core.initial_loop,null);
} else {
return slug_fest.core.request_id = slug_fest.core.request_animation_frame_wrapper.call(null,slug_fest$core$game_over,current_time);
}
} else {
return slug_fest.core.request_id = slug_fest.core.request_animation_frame_wrapper.call(null,slug_fest$core$game_over,current_time);
}
});
/**
 * Initial loop that waits for user input in order to begin the game. This loop should be callled
 * with request-animation-frame-wrapper so that current-time and previous-time will be given proper values. current-time is provided by requstAnimationFrame
 */
slug_fest.core.initial_loop = (function slug_fest$core$initial_loop(current_time,previous_time){
var previous_time__$1 = ((cljs.core._EQ_.call(null,previous_time,null))?current_time:previous_time);
var dt = (current_time - previous_time__$1);
var chi = 0.5;
var alive_shroom_pool = cljs.core.remove.call(null,((function (previous_time__$1,dt,chi){
return (function (shroom){
return shroom.dead_QMARK_;
});})(previous_time__$1,dt,chi))
,slug_fest.core.shroom_pool);
var alive_slug_pool = cljs.core.remove.call(null,((function (previous_time__$1,dt,chi,alive_shroom_pool){
return (function (slug){
return slug.dead_QMARK_;
});})(previous_time__$1,dt,chi,alive_shroom_pool))
,slug_fest.core.slug_pool);
slug_fest.core.render.call(null);

slug_fest.core.update_controls.call(null);

slug_fest.core.game_over_text.material.opacity = (0);

slug_fest.core.you_win_text.material.opacity = (0);

if((!(cljs.core.empty_QMARK_.call(null,alive_shroom_pool))) && (!(cljs.core.empty_QMARK_.call(null,alive_slug_pool)))){
cljs.core.doall.call(null,cljs.core.map.call(null,((function (previous_time__$1,dt,chi,alive_shroom_pool,alive_slug_pool){
return (function (slug){
slug.seek_nearest_shroom(alive_shroom_pool);

return slug.eat(alive_shroom_pool,dt);
});})(previous_time__$1,dt,chi,alive_shroom_pool,alive_slug_pool))
,alive_slug_pool));
} else {
}

if(cljs.core.truth_((function (){var or__4066__auto__ = (slug_fest.core.key_state[slug_fest.core.left_arrow]);
if(cljs.core.truth_(or__4066__auto__)){
return or__4066__auto__;
} else {
return (slug_fest.core.key_state[slug_fest.core.a_key]);
}
})())){
slug_fest.core.hero.move_left(slug_fest.core.ground);
} else {
}

if(cljs.core.truth_((function (){var or__4066__auto__ = (slug_fest.core.key_state[slug_fest.core.up_arrow]);
if(cljs.core.truth_(or__4066__auto__)){
return or__4066__auto__;
} else {
return (slug_fest.core.key_state[slug_fest.core.w_key]);
}
})())){
slug_fest.core.hero.move_up(slug_fest.core.ground);
} else {
}

if(cljs.core.truth_((function (){var or__4066__auto__ = (slug_fest.core.key_state[slug_fest.core.right_arrow]);
if(cljs.core.truth_(or__4066__auto__)){
return or__4066__auto__;
} else {
return (slug_fest.core.key_state[slug_fest.core.d_key]);
}
})())){
slug_fest.core.hero.move_right(slug_fest.core.ground);
} else {
}

if(cljs.core.truth_((function (){var or__4066__auto__ = (slug_fest.core.key_state[slug_fest.core.down_arrow]);
if(cljs.core.truth_(or__4066__auto__)){
return or__4066__auto__;
} else {
return (slug_fest.core.key_state[slug_fest.core.s_key]);
}
})())){
slug_fest.core.hero.move_down(slug_fest.core.ground);
} else {
}

slug_fest.core.hero.update_shaker_box();

if(cljs.core.truth_((slug_fest.core.key_state[slug_fest.core.space_key]))){
slug_fest.core.hero.salt(slug_fest.core.find_nearest_object.call(null,slug_fest.core.hero,slug_fest.core.slug_pool),dt);
} else {
slug_fest.core.hero.stop_salting();
}

if(cljs.core.not.call(null,slug_fest.core.hero.salting)){
slug_fest.core.hero.increment_frame_display_time(dt);
} else {
slug_fest.core.hero.increment_animation_frame();
}

if(cljs.core.empty_QMARK_.call(null,alive_shroom_pool)){
slug_fest.core.game_over_text.material.opacity = (1);

cancelAnimationFrame(slug_fest.core.request_id);

return slug_fest.core.request_id = slug_fest.core.request_animation_frame_wrapper.call(null,slug_fest.core.game_over,null);
} else {
if(cljs.core.empty_QMARK_.call(null,alive_slug_pool)){
slug_fest.core.you_win_text.material.opacity = (1);

cancelAnimationFrame(slug_fest.core.request_id);

return slug_fest.core.request_id = slug_fest.core.request_animation_frame_wrapper.call(null,slug_fest.core.game_over,null);
} else {
return slug_fest.core.request_id = slug_fest.core.request_animation_frame_wrapper.call(null,slug_fest$core$initial_loop,current_time);

}
}
});
slug_fest.core.floor = (function (){var floor_geometry = slug_fest.core.create_plane_geometry.call(null,(1000),(1000),(10),(10));
var floor_texture = slug_fest.core.create_tiled_texture.call(null,"resources/images/checkerboard.jpg",(10),(10));
var floor_material = slug_fest.core.create_mesh_basic_material.call(null,(function (){var obj21236 = {"map":floor_texture,"side":THREE.DoubleSide};
return obj21236;
})());
var floor = (new THREE.Mesh(floor_geometry,floor_material));
return floor;
})();
slug_fest.core.init = (function slug_fest$core$init(){
var skybox = (function (){var skybox_geometry = slug_fest.core.create_box_geometry.call(null,(10000),(10000),(10000));
var skybox_material = slug_fest.core.create_mesh_basic_material.call(null,(function (){var obj21242 = {"color":(8081953),"side":THREE.BackSide};
return obj21242;
})());
return (new THREE.Mesh(skybox_geometry,skybox_material));
})();
slug_fest.core.scene.add(slug_fest.core.ground.mesh);

slug_fest.core.scene.add(slug_fest.core.game_over_text.mesh);

slug_fest.core.scene.add(slug_fest.core.you_win_text.mesh);

slug_fest.core.scene.add(skybox);

slug_fest.core.scene.add(slug_fest.core.hero.mesh);

slug_fest.core.slug_pool = slug_fest.core.create_slug_pool.call(null);

slug_fest.core.shroom_pool = slug_fest.core.create_shroom_pool.call(null);

slug_fest.core.hero.mesh.position.x = (0);

slug_fest.core.hero.mesh.position.y = (150);

cljs.core.doall.call(null,cljs.core.map.call(null,((function (skybox){
return (function (slug){
return slug_fest.core.scene.add(slug.mesh);
});})(skybox))
,slug_fest.core.slug_pool));

cljs.core.doall.call(null,cljs.core.map.call(null,((function (skybox){
return (function (shroom){
return slug_fest.core.scene.add(shroom.mesh);
});})(skybox))
,slug_fest.core.shroom_pool));

THREEx.WindowResize(slug_fest.core.renderer,slug_fest.core.camera);

THREEx.FullScreen.bindKey((function (){var obj21244 = {"charCode":"m".charCodeAt((0))};
return obj21244;
})());

slug_fest.core.request_id = slug_fest.core.request_animation_frame_wrapper.call(null,slug_fest.core.initial_loop,null);

addEventListener("keydown",slug_fest.core.game_key_down_BANG_,true);

return addEventListener("keyup",slug_fest.core.game_key_up_BANG_,true);
});
goog.exportSymbol('slug_fest.core.init', slug_fest.core.init);
