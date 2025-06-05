# ProjectTagless

This is a somewhat revision of dusty developments with giving a nicer look to the repository (previously the project was divided into 2 separate repos and there was no readme to explain each). Probably something even may not be building or working properly. So it s just for th sake of publishing i guess

Tagless was developed almost 3 years ago as a concept of the application that helps to solve the problem of visual pollution. 
Volunteers can easily track "polluted" places, find the new ones, and later clean them up in real life. 
Local authorities are considered as capital and volunteer sponsors of the project.


## Objects that are classified as visual pollution within the concept:
- Tags - primitive form of graffitis
- Paper ads, that are misplaced in cities
- Illegal stuff such as ads of drug stores, drug jobs

Here are few examples:

- Tag, should be considered bad (my opinion) 
<img src="https://upload.wikimedia.org/wikipedia/commons/thumb/e/e9/Painted_wall_and_door_full_of_urban_graffiti_and_glued_street_art%3B_free_photo_Amsterdam_city_of_Fons_Heijnsbroek%2C_01-2022.jpg/1200px-Painted_wall_and_door_full_of_urban_graffiti_and_glued_street_art%3B_free_photo_Amsterdam_city_of_Fons_Heijnsbroek%2C_01-2022.jpg" alt="drawing" style="width:200px; height:200px;"/>
- More like street art, looks decent
  <img src="https://static.mk.ru/upload/entities/2021/10/06/13/articles/detailPicture/3b/f9/24/cf/affedeeab8ee21f9622581d827cea3bf.jpg" alt="drawing" style="width:200px; height:200px;"/>
- Drug ads
<img src="https://cdn.iz.ru/sites/default/files/styles/900x506/public/photo_item-2023-01/2_2_3.jpg?itok=Nv_OWKNu" style="width:200px; height:200px;"/>
- Paper ads
<img src="https://www.zelenograd.ru/img/b/8/e/b8ea2eeed73b61a7aed9163a8d5298cf.jpg" style="width:200px; height:200px;"/>

Tagless consists of plain client and server parts and has the following functionality:
- Pollution markers shown on GoogleMaps
- Ability to CRUD markers
- Ability to vote for marker to be erased
(Every defined period of time a certain number of most voted places will be cleaned up, deleted from the map, and the process starts all over again)
- About screen with already described ideas and "supposed" links to social media 
