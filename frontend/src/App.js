import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Login from './components/login';
import PerfilAdmin from './components/perfilAdmin';
import Home from './components/Home';
import PerfilUsuario from './components/perfilUsuario';
import Equipo from './components/equipo';
import GestionarUsuario from './components/gestionUsuario';
import NuevoUsuario from './components/nuevoUsuario';
import ModificarUsuario from './components/modificarUsuario';
import EmpleadoForm from './components/EmpleadoForm';
import ActaForm from './components/ActaForm';
import ComprasForm from './components/ComprasForm';
import InformeModulo from './components/InformeModulo';

function App() {

  return (
    <Router>
      <Routes>
        {/* otras rutas */}
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/perfilAdmin" element={<PerfilAdmin />} />
        <Route path="/perfilUsuario" element={<PerfilUsuario />} />
        <Route path='/equipo' element={<Equipo />} />
        <Route path='/gestionUsuario' element={<GestionarUsuario />} />
        <Route path='/nuevoUsuario' element={<NuevoUsuario />} />
        <Route path='/modificarUsuario' element={<ModificarUsuario />} />
        <Route path='/EmpleadoForm' element={<EmpleadoForm />} />
        <Route path='/ActaForm' element={<ActaForm />} />
        <Route path='/ComprasForm' element={<ComprasForm />} />
        <Route path='/InformeModulo' element={<InformeModulo />} />
        {/* otras rutas */}
      </Routes>
    </Router>
  );
}

export default App;