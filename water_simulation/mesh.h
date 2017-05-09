/* Polygon mesh class, stores a list of polygons. Borrows heavily from 
proj 4 starter code. */

struct Vertex {
	Vector3D pos;
	Vector3D normal;
}

struct Polygon {
	std::vector<size_t> v_indices; // Used to iterate through polygons
	std::vector<size_t> n_indices;
}

typedef std::vector<Polygon> Polygons;
typedef Polygons::iterator PolygonIter;

struct Mesh : Instance { 
}