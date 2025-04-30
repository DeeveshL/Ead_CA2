using EAD2_CA2_POKEMON.Models;

public class CollectionCard
{
    public Guid CollectionId { get; set; }
    public Guid CardId { get; set; }

    public string FirebaseUserId { get; set; } = string.Empty;

    public Collection Collection { get; set; } = null!;
    public Cards Card { get; set; } = null!;
}
