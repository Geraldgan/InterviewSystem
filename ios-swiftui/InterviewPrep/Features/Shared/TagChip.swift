import SwiftUI

struct TagChip: View {
    let title: String

    var body: some View {
        Text(title)
            .font(.caption.weight(.semibold))
            .padding(.horizontal, 10)
            .padding(.vertical, 6)
            .background(Color.orange.opacity(0.12))
            .foregroundStyle(.brown)
            .clipShape(Capsule())
    }
}
