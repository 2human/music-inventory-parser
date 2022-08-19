Parse Music Entries
===================

An application for parsing music inventories recorded with Microsoft Word into cellular format using Java, Apache POI, and MySQL.

Introduction
------------

Music inventories are comprised of three types of data: collections, sources, and entries.

* Collections constitute a group of sources at a specific location, e.g. a library or a personal collection of musical sources. This data type 
will contain a broad description pertaining to a musical collection as a whole. Fields include collection name, and collection description.

* Sources are individual books of musical information contained within a collection. A collection may contain hundreds of sources. Fields include the collection
within which it is contained, the author, its title, inscriptions, call number, and a miscellaneous information about the source known as its description.

* Entries are individual musical pieces contained within musical sources. Sources may contain many entries, or none at all. Data types include the entry's location 
within its source (e.g, page number), title, author, vocal part, key, melodic incipit (musical nottes), and text incipit (lyrics).

[Source Example](docs/source-example.jpg)
