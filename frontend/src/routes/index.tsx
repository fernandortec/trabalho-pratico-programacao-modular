import { Link, createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/')({ component: Dashboard })

const pages = [
  ['Pacientes', '/pacientes'],
  ['Profissionais', '/profissionais'],
  ['Consultas', '/consultas'],
  ['Internações', '/internacoes'],
  ['Quartos', '/quartos'],
  ['Informações', '/informacoes'],
] as const

function Dashboard() {
  return (
    <main className="p-8">
      <h1 className="text-4xl font-bold">Sistema Hospitalar</h1>
      <p className="mt-4">Selecione uma tela:</p>
      <nav className="mt-4 flex flex-col items-start gap-2">
        {pages.map(([label, to]) => (
          <Link key={to} to={to}>
            {label}
          </Link>
        ))}
      </nav>
    </main>
  )
}
