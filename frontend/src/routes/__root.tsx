import { HeadContent, Link, Scripts, createRootRoute } from '@tanstack/react-router'
import { HospitalProvider } from '../hospital'
import appCss from '../styles.css?url'

const navigation = [
  { to: '/', label: 'Painel', icon: '◫', exact: true },
  { to: '/pacientes', label: 'Pacientes', icon: '♙' },
  { to: '/profissionais', label: 'Profissionais', icon: '⚕' },
  { to: '/consultas', label: 'Consultas', icon: '▦' },
  { to: '/internacoes', label: 'Internações', icon: '▣' },
  { to: '/quartos', label: 'Quartos', icon: '⌂' },
  { to: '/informacoes', label: 'Informações', icon: '≋' },
] as const

export const Route = createRootRoute({
  head: () => ({
    meta: [
      { charSet: 'utf-8' },
      { name: 'viewport', content: 'width=device-width, initial-scale=1' },
      { title: 'VitaCare · Gestão hospitalar' },
    ],
    links: [{ rel: 'stylesheet', href: appCss }],
  }),
  shellComponent: RootDocument,
})

function RootDocument({ children }: { children: React.ReactNode }) {
  return (
    <html lang="pt-BR">
      <head><HeadContent /></head>
      <body>
        <HospitalProvider>
          <div className="app-shell">
            <aside className="sidebar">
              <Link to="/" className="brand-lockup">
                <span className="brand-mark">+</span>
                <span><strong>VitaCare</strong><small>GESTÃO HOSPITALAR</small></span>
              </Link>
              <div className="nav-caption">MENU PRINCIPAL</div>
              <nav className="side-nav" aria-label="Navegação principal">
                {navigation.map(({ to, label, icon, exact }) => (
                  <Link
                    key={to}
                    to={to}
                    activeOptions={exact ? { exact: true } : undefined}
                    className="nav-link"
                    activeProps={{ className: 'nav-link active' }}
                  >
                    <span className="nav-icon" aria-hidden="true">{icon}</span><span>{label}</span>
                  </Link>
                ))}
              </nav>
              <div className="sidebar-bottom">
                <div className="online-indicator"><span /> Ambiente de demonstração</div>
                <p>Os dados ficam salvos neste navegador.</p>
              </div>
            </aside>
            <div className="main-column">
              <header className="topbar">
                <div><span className="topbar-label">HOSPITAL SANTA HELENA</span><strong>Unidade Central</strong></div>
                <div className="user-chip">
                  <span className="user-avatar">AD</span>
                  <span><strong>Administração</strong><small>Gestor do sistema</small></span>
                  <span className="chevron" aria-hidden="true">⌄</span>
                </div>
              </header>
              <main className="page-content">{children}</main>
            </div>
          </div>
        </HospitalProvider>
        <Scripts />
      </body>
    </html>
  )
}
