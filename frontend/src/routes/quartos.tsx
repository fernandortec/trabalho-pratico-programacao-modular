import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/quartos')({ component: Quartos })

function Quartos() {
  return (
    <main className="p-8">
      <h1 className="text-4xl font-bold">Quartos</h1>
      <p className="mt-4">Tela de quartos hospitalares.</p>
    </main>
  )
}
