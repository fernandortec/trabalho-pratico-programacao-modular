import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/internacoes')({ component: Internacoes })

function Internacoes() {
  return (
    <main className="p-8">
      <h1 className="text-4xl font-bold">Internações</h1>
      <p className="mt-4">Tela de internações.</p>
    </main>
  )
}
