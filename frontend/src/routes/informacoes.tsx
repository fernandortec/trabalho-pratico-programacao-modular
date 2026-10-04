import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/informacoes')({ component: Informacoes })

function Informacoes() {
  return (
    <main className="p-8">
      <h1 className="text-4xl font-bold">Informações</h1>
      <p className="mt-4">Tela de informações médicas e administrativas.</p>
    </main>
  )
}
